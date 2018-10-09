package br.com.eits.queryexport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import br.com.eits.queryexport.exporter.CSVExporter;
import br.com.eits.queryexport.exporter.PDFExporter;
import br.com.eits.queryexport.exporter.XLSExporter;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public class DummyExport
{
	public static void main( String[] args ) throws Exception
	{
		val exportConfig = new QueryExportConfiguration( "title", null );
		val messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename( "classpath:i18n/strings" );
		val queryExporter = new QueryExporter(
				Collections.singleton( new DummyEntityRepository() ),
				new HashSet<>( Arrays.asList( new CSVExporter( messageSource ), new XLSExporter( messageSource ), new PDFExporter( exportConfig, messageSource ) ) ),
				messageSource
		);
		LocaleContextHolder.setLocale( new Locale( "pt" ) );
		val query = queryExporter.export(
				DummyEntity.class,
				new PageImpl<>( Arrays.asList(
						new DummyEntity( 1L, "um um um um um um um um um um um um um um", true, new BigDecimal( "2.50" ), DummyEntity.DummyEntityStatus.AUTHORIZED ),
						new DummyEntity( 2L, "dois", false, new BigDecimal( "2.50" ), DummyEntity.DummyEntityStatus.PENDING ),
						new DummyEntity( 3L, "tres", null, new BigDecimal( "1500.50" ), null )
				) ),
				"test"
		);
		val baos = query.to( QueryExportFormat.PDF ).getOutputStream();
		val bais = new ByteArrayInputStream( baos.toByteArray() );
		val file = new File( "/tmp/test-output.pdf" );
		file.delete();
		file.createNewFile();
		val output = new FileOutputStream( file );
		IOUtils.copy( bais, output );
	}

	interface IDummyEntityRepository extends JpaRepository<DummyEntity, Long>
	{
		@ExportableQuery(
				id = "test",
				name = "dummyEntity.query",
				fields = {
						@Field("id"),
						@Field("name"),
						@Field("enabled"),
						@Field("price"),
						@Field("status")
				}
		)
		List<DummyEntity> query();
	}

	static class DummyEntityRepository implements IDummyEntityRepository
	{
		@Override
		public List<DummyEntity> findAll()
		{
			return null;
		}

		@Override
		public List<DummyEntity> findAll( Sort sort )
		{
			return null;
		}

		@Override
		public Page<DummyEntity> findAll( Pageable pageable )
		{
			return null;
		}

		@Override
		public List<DummyEntity> findAll( Iterable<Long> iterable )
		{
			return null;
		}

		@Override
		public long count()
		{
			return 0;
		}

		@Override
		public void delete( Long aLong )
		{

		}

		@Override
		public void delete( DummyEntity dummyEntity )
		{

		}

		@Override
		public void delete( Iterable<? extends DummyEntity> iterable )
		{

		}

		@Override
		public void deleteAll()
		{

		}

		@Override
		public <S extends DummyEntity> S save( S s )
		{
			return null;
		}

		@Override
		public <S extends DummyEntity> List<S> save( Iterable<S> iterable )
		{
			return null;
		}

		@Override
		public DummyEntity findOne( Long aLong )
		{
			return null;
		}

		@Override
		public boolean exists( Long aLong )
		{
			return false;
		}

		@Override
		public void flush()
		{

		}

		@Override
		public <S extends DummyEntity> S saveAndFlush( S s )
		{
			return null;
		}

		@Override
		public void deleteInBatch( Iterable<DummyEntity> iterable )
		{

		}

		@Override
		public void deleteAllInBatch()
		{

		}

		@Override
		public DummyEntity getOne( Long aLong )
		{
			return null;
		}

		@Override
		public <S extends DummyEntity> S findOne( Example<S> example )
		{
			return null;
		}

		@Override
		public <S extends DummyEntity> List<S> findAll( Example<S> example )
		{
			return null;
		}

		@Override
		public <S extends DummyEntity> List<S> findAll( Example<S> example, Sort sort )
		{
			return null;
		}

		@Override
		public <S extends DummyEntity> Page<S> findAll( Example<S> example, Pageable pageable )
		{
			return null;
		}

		@Override
		public <S extends DummyEntity> long count( Example<S> example )
		{
			return 0;
		}

		@Override
		public <S extends DummyEntity> boolean exists( Example<S> example )
		{
			return false;
		}

		@Override
		public List<DummyEntity> query()
		{
			return null;
		}
	}
}
