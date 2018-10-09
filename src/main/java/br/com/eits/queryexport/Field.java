package br.com.eits.queryexport;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field
{
	String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";

	/**
	 * Nome do campo, com a mesma sintaxe do EntityGraph.
	 */
	String value() default "";

	/**
	 * Largura percentual do campo. Um valor de 0 faz com que a largura seja calculada automaticamente.
	 */
	int width() default 0;

	/**
	 * Pattern para exibição de data/hora.
	 */
	String pattern() default DATE_TIME_PATTERN;

	/**
	 * Usar máscaras e formatação (separação de milhares) nos números.
	 */
	boolean format() default true;
}
