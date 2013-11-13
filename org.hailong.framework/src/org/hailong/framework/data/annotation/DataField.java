package org.hailong.framework.data.annotation;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.ANNOTATION_TYPE})
public @interface DataField {
	public String value();
	public DataFieldType type() default DataFieldType.VARCHAR;
	public int length() default 0;
	public boolean index() default false;
}
