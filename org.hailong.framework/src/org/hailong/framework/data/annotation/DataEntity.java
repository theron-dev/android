package org.hailong.framework.data.annotation;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
public @interface DataEntity {
	public String value();
	public DataField[] fields() default {};
}
