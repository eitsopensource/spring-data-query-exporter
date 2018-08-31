package br.com.eits.queryexport;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ExportableQuery
{
	/**
	 * ID da query exportável, usado para referenciá-la na exportação.
	 */
	String id();

	/**
	 * Nome da consulta.
	 */
	String name();

	/**
	 * Campos da query exportável, sintaxe igual ao @EntityGraph. Caso não esteja declarada, será usado o EntityGraph
	 * menos o campo de ID da entidade.
	 */
	Field[] fields() default {};
}
