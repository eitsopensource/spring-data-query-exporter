# Integrar a um projeto

Em uma classe de configuração:

1. Adicionar a anotação ```@EnableQueryExporter```
2. Definir um bean do tipo ```QueryExportConfiguration```, conforme exemplo:

```java
@Bean
	public QueryExportConfiguration queryExportConfiguration(
			@Value("${singra.reports.title}") String reportTitle,
			@Value("${singra.reports.image}") String reportImage
	)
	{
		return new QueryExportConfiguration( reportTitle, reportImage );
	}
```

O parâmetro ```reportTitle``` é obrigatório.

#### DWR

Adicionar a seguinte tag no ```dwr.xml``` para habilitar a conversão do tipo ```QueryExportFormat```:

```xml
<convert converter="enum" match="br.com.eits.queryexport.QueryExportFormat" />
```

# Uso

Este componente permite com que qualquer consulta de um repositório do Spring Data seja exportada para CSV, XLS ou PDF. 
A classe principal é QueryExporter, que deve ser injetada em seu serviço via @Autowired. Após a definição das consultas, 
a sintaxe é simples:

```java
@Transactional(readOnly = true)
public FileTransfer listUsuariosByFiltersExport( 
    String filter, 
    PageRequest pageable, 
    QueryExportFormat format )
{
    Page<Usuario> page = this.usuarioRepository.listByFilters( 
        filter, 
        QueryExporter.keepSortOnly( pageable ) );
    return queryExporter.export( 
        Usuario.class, 
        page, 
        IUsuarioRepository.LIST_BY_FILTERS )
            .to( format ).fileTransfer();
}
```

Definição no repositório:

```java
public interface IUsuarioRepository extends JpaRepository<Usuario, Long>
{
    public static final String LIST_BY_FILTERS = "listByFilters";

    /**
     * @param filter
     * @param pageable
     * @return
     */
    @ExportableQuery(
            id = LIST_BY_FILTERS,
            name = "usuario.query.listByFilters"
    )
    @EntityGraph(attributePaths = {"id", "nome", "login", "email"})
    @Query(value = "FROM Usuario usuario " +
            "WHERE ( FILTER(usuario.id, :filter) = TRUE "
            + "OR FILTER(usuario.nome, :filter) = TRUE "
            + "OR FILTER(usuario.login, :filter) = TRUE)")
    public Page<Usuario> listByFilters( @Param("filter") String filter, Pageable pageable );
}
```

Para cada consulta a ser exportada, é necessário anotar seu método no repositório com a anotação @ExportableQuery. Dois parâmetros são obrigatórios: id e name. id é o identificador da consulta, usado ao chamá-la. Recomendo definir esse valor em uma constante para evitar erros ao refatorar. name é o título do relatório, que será traduzido pelo MessageSourceHolder.

Para definir os campos da consulta, são usados ou as anotações @Field, que são definidas no parâmetro fields da anotação @ExportableQuery, ou os campos da anotação @EntityGraph.

Se não forem definidos campos pelo parâmetro fields, a exportação em PDF atribuirá automaticamente larguras iguais para todos os campos. Para definir a largura, é necessário especificar a largura percentual manualmente. Tomando o exemplo anterior:

```java
@ExportableQuery(
        id = LIST_BY_FILTERS,
        name = "usuario.query.listByFilters",
        fields = {
            @Field(value = "nome", width = 40),
            @Field(value = "login", width = 20),
            @Field(value = "email", width = 40)}
)
```