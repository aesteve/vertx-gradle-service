package com.github.aesteve.vertx.gradle;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.net.URL;

import org.gradle.tooling.model.GradleProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class GradleServiceTestBase {

	protected Vertx vertx;
	protected GradleService gradle;
	protected final URL simpleProj = this.getClass().getResource("/simple-java");

	@Before
	public void setUp() {
		vertx = Vertx.vertx();
		gradle = GradleService.create(vertx);
	}

	@After
	public void tearDown() {
		if (vertx != null) {
			vertx.close();
		}
	}

	@Test
	public void getProject(TestContext context) throws Exception {
		Async async = context.async();
		gradle.project(simpleProj, res -> {
			context.assertFalse(res.failed());
			GradleProject project = res.result();
			context.assertNotNull(project);
			async.complete();
		});
	}

	@Test
	public void launchTask(TestContext context) throws Exception {
		Async async = context.async();
		StringBuilder out = new StringBuilder();
		StringBuilder progress = new StringBuilder();
		String address = gradle.runTask(simpleProj, "clean", res -> {
			context.assertFalse(res.failed());
			context.assertTrue(out.length() > 0);
			context.assertTrue(progress.length() > 0);
			System.out.println("--- OUT ---");
			System.out.println(out.toString());
			System.out.println("--- PROGRESS ---");
			System.out.println(progress.toString());
			async.complete();
		});
		vertx.eventBus().consumer(address + ".out", message -> {
			Object body = message.body();
			context.assertTrue(body instanceof String);
			out.append(body.toString() + "\n");
		});
		vertx.eventBus().consumer(address + ".progress", message -> {
			String eventTime = message.headers().get("event-time");
			context.assertNotNull(eventTime);
			Object body = message.body();
			context.assertTrue(body instanceof String);
			progress.append(eventTime + ":" + message.body().toString() + "\n");
		});
	}

	@Test
	public void failTask(TestContext context) throws Exception {
		Async async = context.async();
		gradle.runTask(simpleProj, "doesNotExist", res -> {
			context.assertNotNull(res.cause());
			async.complete();
		});
	}

}
