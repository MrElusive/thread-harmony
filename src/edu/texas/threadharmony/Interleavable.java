package edu.texas.threadharmony;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Interleavable {
	int numberOfThreads() default 1;
	int numberOfInterleaves() default 0;
}
