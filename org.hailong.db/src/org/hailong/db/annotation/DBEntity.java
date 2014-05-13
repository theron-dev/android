package org.hailong.db.annotation;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
public @interface DBEntity {
	
	public String value();
	
	public String dataKey() default "rowId";
	
	public DBField[] fields() default {};
	
}
