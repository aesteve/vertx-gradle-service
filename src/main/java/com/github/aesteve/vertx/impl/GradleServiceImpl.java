package com.github.aesteve.vertx.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;
import org.gradle.tooling.model.GradleProject;

import com.github.aesteve.vertx.GradleService;
import com.github.aesteve.vertx.io.EventBusOutputStream;
import com.github.aesteve.vertx.io.EventBusProgressListener;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class GradleServiceImpl implements GradleService {

	private Vertx vertx;
	
	public GradleServiceImpl(Vertx vertx) {
		this.vertx = vertx;
	}
	
	@Override
	public void project(URL url, Handler<AsyncResult<GradleProject>> handler) {
		File f;
		try {
			f = new File(url.toURI());
		} catch(URISyntaxException use) {
			handler.handle(Future.failedFuture(use));
			return;
		}
		project(f, handler);

	}

	@Override
	public void project(File file, Handler<AsyncResult<GradleProject>> handler) {
		vertx.executeBlocking(future -> {
			GradleConnector connector = GradleConnector.newConnector();
			connector.forProjectDirectory(file);
			ProjectConnection conn = connector.connect();
			future.complete(conn.getModel(GradleProject.class));
		}, handler);		
	}
	
	@Override
	public String runTask(File file, String taskName, Handler<AsyncResult<Void>> handler) {
		return runTask(file, taskName, null, null, handler);
	}

	@Override
	public String runTask(URL url, String taskName, Handler<AsyncResult<Void>> handler) {
		return runTask(url, taskName, null, null, handler);
	}
	
	@Override
	public String runTask(File file, String taskName, String javaHome, Handler<AsyncResult<Void>> handler) {
		return runTask(file, taskName, javaHome, null, handler);
	}

	@Override
	public String runTask(URL url, String taskName, String javaHome, Handler<AsyncResult<Void>> handler) {
		return runTask(url, taskName, javaHome, null, handler);
	}

	@Override
	public String runTask(File file, String taskName, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		return runTask(file, taskName, null, jvmArguments, handler);
	}

	@Override
	public String runTask(URL url, String taskName, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		return runTask(url, taskName, null, jvmArguments, handler);
	}

	@Override
	public String runTasks(URL url, List<String> taskNames, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		File f;
		try {
			f = new File(url.toURI());
		} catch(URISyntaxException use) {
			handler.handle(Future.failedFuture(use));
			return null;
		}
		return runTasks(f, taskNames, javaHome, jvmArguments, handler);
	}
	
	@Override
	public String runTasks(File file, List<String> taskNames, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		String identifier = "gradle.task." + taskNames + "." + String.valueOf(new Date().getTime());
		GradleConnector connector = GradleConnector.newConnector();
		connector.forProjectDirectory(file);
		ProjectConnection conn = connector.connect();
		BuildLauncher launcher = conn.newBuild();
		EventBusOutputStream out = new EventBusOutputStream(vertx, identifier + ".out");
		EventBusOutputStream err = new EventBusOutputStream(vertx, identifier + ".err");
		launcher.setStandardOutput(out);
		launcher.setStandardError(err);
		launcher.addProgressListener(new EventBusProgressListener(vertx, identifier + ".progress"));
		if (javaHome != null) {
			launcher.setJavaHome(null);
		}
		if (jvmArguments != null) {
			launcher.setJvmArguments(jvmArguments);
		}
		launcher.forTasks(taskNames.toArray(new String[0])).run(resultHandler(handler));
		return identifier;
	}
	
	@Override
	public String runTasks(File file, List<String> taskNames, Handler<AsyncResult<Void>> handler) {
		return runTasks(file, taskNames, null, null, handler);
	}

	@Override
	public String runTasks(URL url, List<String> taskNames, Handler<AsyncResult<Void>> handler) {
		return runTasks(url, taskNames, null, null, handler);
	}

	@Override
	public String runTasks(File file, List<String> taskNames, String javaHome, Handler<AsyncResult<Void>> handler) {
		return runTasks(file, taskNames, javaHome, null, handler);
	}

	@Override
	public String runTasks(URL url, List<String> taskNames, String javaHome, Handler<AsyncResult<Void>> handler) {
		return runTasks(url, taskNames, javaHome, handler);
	}

	@Override
	public String runTasks(File file, List<String> taskNames, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		return runTasks(file, taskNames, null, jvmArguments, handler);
	}

	@Override
	public String runTasks(URL url, List<String> taskNames, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		return runTasks(url, taskNames, null, jvmArguments, handler);
	}

	@Override
	public String runTask(File file, String taskName, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		return runTasks(file, Arrays.asList(new String[]{taskName}), javaHome, jvmArguments, handler);
	}

	@Override
	public String runTask(URL url, String taskName, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler) {
		return runTasks(url, Arrays.asList(new String[]{taskName}), javaHome, jvmArguments, handler);
	}

	private static ResultHandler<Void> resultHandler(Handler<AsyncResult<Void>> handler) {
		return new ResultHandler<Void>() {
			public void onComplete(Void result) {
				handler.handle(Future.succeededFuture());
			}
			public void onFailure(GradleConnectionException gce) {
				handler.handle(Future.failedFuture(gce));
			}
		};
	}

}
