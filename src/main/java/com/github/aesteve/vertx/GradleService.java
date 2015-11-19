package com.github.aesteve.vertx;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.gradle.tooling.model.GradleProject;

import com.github.aesteve.vertx.impl.GradleServiceImpl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public interface GradleService {
	
	public static GradleService create(Vertx vertx) {
		return new GradleServiceImpl(vertx);
	}
	
	public void project(URL url, Handler<AsyncResult<GradleProject>> handler);
	public void project(File file, Handler<AsyncResult<GradleProject>> handler);
	public String runTasks(File file, List<String> taskNames, Handler<AsyncResult<Void>> handler);
	public String runTask(File file, String taskName, Handler<AsyncResult<Void>> handler);
	public String runTasks(URL url, List<String> taskNames, Handler<AsyncResult<Void>> handler);
	public String runTask(URL url, String taskName, Handler<AsyncResult<Void>> handler);
	public String runTasks(File file, List<String> taskNames, String javaHome, Handler<AsyncResult<Void>> handler);
	public String runTask(File file, String taskName, String javaHome, Handler<AsyncResult<Void>> handler);
	public String runTasks(URL url, List<String> taskNames, String javaHome, Handler<AsyncResult<Void>> handler);
	public String runTask(URL url, String taskName, String javaHome, Handler<AsyncResult<Void>> handler);
	public String runTasks(File file, List<String> taskNames, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTask(File file, String taskName, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTasks(URL url, List<String> taskNames, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTask(URL url, String taskName, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTasks(File file, List<String> taskNames, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTask(File file, String taskName, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTasks(URL url, List<String> taskNames, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	public String runTask(URL url, String taskName, String javaHome, Iterable<String> jvmArguments, Handler<AsyncResult<Void>> handler);
	
}
