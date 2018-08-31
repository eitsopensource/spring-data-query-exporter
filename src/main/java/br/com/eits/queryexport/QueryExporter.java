package br.com.eits.queryexport;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import br.com.eits.queryexport.exporter.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public class QueryExporter
{
	private final Set<JpaRepository<?, ?>> jpaRepositories;

	private final Set<Exporter> exporters;

	private final MessageSource messageSource;

	/**
	 *
	 */
	private final Map<Class<?>, JpaRepository<?, ?>> entityRepositoryClassCache = new ConcurrentHashMap<>();

	public QueryExporter( @Autowired(required = false) Set<JpaRepository<?, ?>> jpaRepositories, Set<Exporter> exporters, MessageSource messageSource )
	{
		this.jpaRepositories = jpaRepositories;
		this.exporters = exporters;
		this.messageSource = messageSource;
	}

	public static Pageable keepSortOnly( Pageable input )
	{
		return input == null ? null : new PageRequest( 0, Integer.MAX_VALUE, input.getSort() );
	}

	public ExportedQuery export( Class<?> entityClass, Page<?> queryResult, String queryName )
	{
		final Method method = getExportableMethodByQueryName( getRepositoryForClass( entityClass ), queryName );
		return new ExportedQuery( entityClass, method, queryResult, exporters, messageSource );
	}

	private Method getExportableMethodByQueryName( JpaRepository<?, ?> repository, String queryName )
	{
		return Arrays.stream( repository.getClass().getInterfaces()[0]
				.getMethods() )
				.filter( method -> method.getAnnotation( ExportableQuery.class ) != null && method.getAnnotation( ExportableQuery.class ).id().equals( queryName ) )
				.findFirst().orElseThrow( () -> new IllegalArgumentException( "Não foi encontrada uma query salva com esse nome." ) );
	}

	private JpaRepository<?, ?> getRepositoryForClass( Class<?> entityClass )
	{
		return entityRepositoryClassCache.computeIfAbsent( entityClass, clazz ->
				jpaRepositories.stream()
						.filter( jpaRepository -> isRepositoryForClass( jpaRepository, entityClass ) )
						.findFirst()
						.orElseThrow( () -> new IllegalArgumentException( "Não existe um repositório para esta entidade." ) ) );
	}

	private boolean isRepositoryForClass( JpaRepository<?, ?> repository, Class<?> entityClass )
	{
		try
		{
			ParameterizedType type = (ParameterizedType) repository.getClass().getInterfaces()[0].getGenericInterfaces()[0];
			Class<?> repositoryType = (Class<?>) type.getActualTypeArguments()[0];
			return repositoryType.equals( entityClass );
		}
		catch ( Exception e )
		{
			throw new IllegalStateException( "Não foi possível identificar qual o repositório para a entidade " + entityClass.getSimpleName() + ". Verifique sua hierarquia de classes.", e );
		}
	}
}
