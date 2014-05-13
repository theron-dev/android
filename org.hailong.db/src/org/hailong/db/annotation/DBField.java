package org.hailong.db.annotation;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.ANNOTATION_TYPE})
public @interface DBField {
	public String value();
	public DBFieldType type() default DBFieldType.VARCHAR;
	public int length() default 0;
	public boolean index() default false;
}
