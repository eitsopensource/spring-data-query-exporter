package br.com.eits.queryexport;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.eits.queryexport.exporter.Exporter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;

public class ExportedQuery
{
	private final Class<?> entityClass;
	private final Method method;
	private final Page<?> result;
	private final String queryName;
	private final Set<Exporter> exporters;
	private final MessageSource messageSource;

	ExportedQuery( Class<?> entityClass, Method method, Page<?> result, Set<Exporter> exporters, MessageSource messageSource )
	{
		this.entityClass = entityClass;
		this.method = method;
		this.result = result;
		this.queryName = messageSource.getMessage( method.getAnnotation( ExportableQuery.class ).name(), null, LocaleContextHolder.getLocale() );
		this.exporters = exporters;
		this.messageSource = messageSource;
	}

	public ExportedWrapper to( QueryExportFormat format )
	{
		return new ExportedWrapper(
				format,
				getExporter( format ).export( entityClass, queryName, extractColumns( entityClass, method ), result.getContent() ),
				this.queryName
		);
	}

	private List<QueryColumn> extractColumns( Class<?> entityClass, Method method )
	{
		if ( method.getAnnotation( ExportableQuery.class ).fields().length > 0 )
		{
			return Arrays.stream( method.getAnnotation( ExportableQuery.class ).fields() )
					.map( field -> QueryColumn.of( field.value(), getLocalizedLabelForAttribute( entityClass, field.value() ), field.width(), field.pattern() ) )
					.collect( Collectors.toList() );
		}
		else if ( method.getAnnotation( EntityGraph.class ) != null )
		{
			return Arrays.stream( method.getAnnotation( EntityGraph.class ).attributePaths() )
					.filter( path -> !path.equals( "id" ) )
					.map( path -> QueryColumn.of( path, getLocalizedLabelForAttribute( entityClass, path ), 0, Field.DATE_TIME_PATTERN ) )
					.collect( Collectors.toList() );
		}
		else
		{
			throw new IllegalStateException( "Faltando declaração de campos ou @EntityGraph no repositório da entidade " + entityClass.getSimpleName() + ", método " + method );
		}
	}

	/**
	 * Para Usuario e "nome", retornamos "usuario.nome"
	 */
	private String getLocalizedLabelForAttribute( Class<?> entityClass, String path )
	{
		return messageSource.getMessage( Introspector.decapitalize( entityClass.getSimpleName() ) + "." + path, null, LocaleContextHolder.getLocale() );
	}

	private Exporter getExporter( QueryExportFormat format )
	{
		return exporters.stream()
				.filter( exporter -> format.getExporter().isAssignableFrom( exporter.getClass() ) )
				.findFirst().orElseThrow( () -> new IllegalArgumentException( "Não foi encontrado o exportador para o formato " + format ) );
	}
}
