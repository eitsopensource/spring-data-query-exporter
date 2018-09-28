package br.com.eits.queryexport;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

public final class EntityPropertyExtractor
{
	private static final Pattern PROPERTY_PATTERN = Pattern.compile( "^([a-zA-Z0-9_]+\\.)*[a-zA-Z0-9]+$" );

	private EntityPropertyExtractor()
	{

	}

	public static Class<?> extractPropertyType( Class<?> entityClass, String originalPath )
	{
		Assert.isTrue( PROPERTY_PATTERN.matcher( originalPath ).matches(), "O caminho de propriedade \"" + originalPath + "\" para a entidade " + entityClass.getSimpleName() + " é inválido." );
		try
		{
			Class<?> clazz = entityClass;
			for ( String path : originalPath.split( "\\." ) )
			{
				PropertyDescriptor propertyDescriptor = Arrays.stream( Introspector.getBeanInfo( clazz ).getPropertyDescriptors() )
						.filter( property -> path.equals( property.getName() ) ).findAny()
						.orElseThrow( () -> new IllegalArgumentException( "Não foi encontrada uma propriedade de nome \"" + path + "\" na entidade " + entityClass.getSimpleName() ) );
				clazz = propertyDescriptor.getPropertyType();
			}
			return clazz;
		}
		catch ( Exception e )
		{
			throw new IllegalStateException( "Erro ao ler tipo de propriedade para caminho " + originalPath + " na classe " + entityClass.getSimpleName(), e );
		}
	}

	public static Object extractPropertyValue( Object entity, String path )
	{
		Assert.isTrue( PROPERTY_PATTERN.matcher( path ).matches(), "O caminho de propriedade \"" + path + "\" para a entidade " + entity.getClass().getSimpleName() + " é inválido." );
		try
		{
			if ( !path.contains( "." ) )
			{
				PropertyDescriptor propertyDescriptor = Arrays.stream( Introspector.getBeanInfo( entity.getClass() ).getPropertyDescriptors() )
						.filter( property -> path.equals( property.getName() ) ).findAny()
						.orElseThrow( () -> new IllegalArgumentException( "Não foi encontrada uma propriedade de nome \"" + path + "\" na entidade " + entity.getClass().getSimpleName() ) );
				return propertyDescriptor.getReadMethod().invoke( entity );
			}
			else
			{
				return extractPropertyValue( extractPropertyValue( entity, path.split( "\\." )[0] ), path.substring( path.indexOf( "." ) + 1 ) );
			}
		}
		catch ( NullPointerException e )
		{
			return null;
		}
		catch ( IllegalAccessException | InvocationTargetException | IntrospectionException e )
		{
			throw new IllegalStateException( e );
		}
	}

	public static Class<?> extractPropertyType( Object entity, String path )
	{
		Assert.isTrue( PROPERTY_PATTERN.matcher( path ).matches(), "O caminho de propriedade \"" + path + "\" para a entidade " + entity.getClass().getSimpleName() + " é inválido." );
		try
		{
			if ( !path.contains( "." ) )
			{
				PropertyDescriptor propertyDescriptor = Arrays.stream( Introspector.getBeanInfo( entity.getClass() ).getPropertyDescriptors() )
						.filter( property -> path.equals( property.getName() ) ).findAny()
						.orElseThrow( () -> new IllegalArgumentException( "Não foi encontrada uma propriedade de nome \"" + path + "\" na entidade " + entity.getClass().getSimpleName() ) );
				return propertyDescriptor.getPropertyType();
			}
			else
			{
				return extractPropertyType( extractPropertyValue( entity, path.split( "\\." )[0] ), path.substring( path.indexOf( "." ) + 1 ) );
			}
		}
		catch ( NullPointerException e )
		{
			return null;
		}
		catch ( IntrospectionException e )
		{
			throw new IllegalStateException( e );
		}
	}
}
