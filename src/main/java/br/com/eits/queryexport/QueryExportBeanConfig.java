package br.com.eits.queryexport;

import java.io.IOException;
import java.util.Set;

import br.com.eits.queryexport.exporter.CSVExporter;
import br.com.eits.queryexport.exporter.Exporter;
import br.com.eits.queryexport.exporter.PDFExporter;
import br.com.eits.queryexport.exporter.XLSExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
public class QueryExportBeanConfig
{
	@Bean
	public Exporter pdfExporter( QueryExportConfiguration queryExportConfiguration ) throws IOException
	{
		return new PDFExporter( queryExportConfiguration );
	}

	@Bean
	public Exporter csvExporter()
	{
		return new CSVExporter();
	}

	@Bean
	public Exporter xlsExporter()
	{
		return new XLSExporter();
	}

	@Bean
	public QueryExporter queryExporter( @Autowired(required = false) Set<JpaRepository<?, ?>> jpaRepositories, Set<Exporter> exporters, MessageSource messageSource )
	{
		return new QueryExporter( jpaRepositories, exporters, messageSource );
	}
}
