RAG (Retrieval-Augmented Generation)
LLM's knowledge is limited to the data it has been trained on. If you want to make an LLM aware of domain-specific knowledge or proprietary data, you can:

Use RAG, which we will cover in this section
Fine-tune the LLM with your data
Combine both RAG and fine-tuning
What is RAG?
Simply put, RAG is the way to find and inject relevant pieces of information from your data into the prompt before sending it to the LLM. This way LLM will get (hopefully) relevant information and will be able to reply using this information, which should reduce the probability of hallucinations.

Relevant pieces of information can be found using various information retrieval methods. The most popular are:

Full-text (keyword) search. This method uses techniques like TF-IDF and BM25 to search documents by matching the keywords in a query (e.g., what the user is asking) against a database of documents. It ranks results based on the frequency and relevance of these keywords in each document.
Vector search, also known as "semantic search". Text documents are converted into vectors of numbers using embedding models. It then finds and ranks documents based on the cosine similarity or other similarity/distance measures between the query vector and document vectors, thus capturing deeper semantic meanings.
Hybrid. Combining multiple search methods (e.g., full-text + vector) usually improves the effectiveness of the search.
Currently, this page focuses mostly on vector search. Full-text and hybrid search are currently supported only by Azure AI Search integration, see AzureAiSearchContentRetriever for more details. We plan to expand the RAG toolbox to include full-text and hybrid search in the near future.

RAG Stages
The RAG process is divided into 2 distinct stages: indexing and retrieval. LangChain4j provides tooling for both stages.

Indexing
During the indexing stage, documents are pre-processed in a way that enables efficient search during the retrieval stage.

This process can vary depending on the information retrieval method used. For vector search, this typically involves cleaning the documents, enriching them with additional data and metadata, splitting them into smaller segments (aka chunking), embedding these segments, and finally storing them in an embedding store (aka vector database).

The indexing stage usually occurs offline, meaning it does not require end users to wait for its completion. This can be achieved through, for example, a cron job that re-indexes internal company documentation once a week during the weekend. The code responsible for indexing can also be a separate application that only handles indexing tasks.

However, in some scenarios, end users may want to upload their custom documents to make them accessible to the LLM. In this case, indexing should be performed online and be a part of the main application.

Here is a simplified diagram of the indexing stage:

Retrieval
The retrieval stage usually occurs online, when a user submits a question that should be answered using the indexed documents.

This process can vary depending on the information retrieval method used. For vector search, this typically involves embedding the user's query (question) and performing a similarity search in the embedding store. Relevant segments (pieces of the original documents) are then injected into the prompt and sent to the LLM.

Here is a simplified diagram of the retrieval stage:

RAG Flavours in LangChain4j
LangChain4j offers three flavors of RAG:

Easy RAG: the easiest way to start with RAG
Naive RAG: a basic implementation of RAG using vector search
Advanced RAG: a modular RAG framework that allows for additional steps such as query transformation, retrieval from multiple sources, and re-ranking
Easy RAG
LangChain4j has an "Easy RAG" feature that makes it as easy as possible to get started with RAG. You don't have to learn about embeddings, choose a vector store, find the right embedding model, figure out how to parse and split documents, etc. Just point to your document(s), and LangChain4j will do its magic.

If you need a customizable RAG, skip to the next section.

If you are using Quarkus, there is an even easier way to do Easy RAG. Please read Quarkus documentation.

note
The quality of such "Easy RAG" will, of course, be lower than that of a tailored RAG setup. However, this is the easiest way to start learning about RAG and/or make a proof of concept. Later, you will be able to transition smoothly from Easy RAG to more advanced RAG, adjusting and customizing more and more aspects.

Import the langchain4j-easy-rag dependency:
<dependency>
<groupId>dev.langchain4j</groupId>
<artifactId>langchain4j-easy-rag</artifactId>
<version>1.0.1-beta6</version>
</dependency>

Let's load your documents:
List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j/documentation");

This will load all files from the specified directory.

What is happening under the hood?
How to customize loading documents?
Now, we need to preprocess and store documents in a specialized embedding store, also known as vector database. This is necessary to quickly find relevant pieces of information when a user asks a question. We can use any of our 15+ supported embedding stores, but for simplicity, we will use an in-memory one:
InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
EmbeddingStoreIngestor.ingest(documents, embeddingStore);

What is happening under the hood?
The EmbeddingStoreIngestor loads a DocumentSplitter from the langchain4j-easy-rag dependency through SPI. Each Document is split into smaller pieces (TextSegments) each consisting of no more than 300 tokens and with a 30-token overlap.

The EmbeddingStoreIngestor loads an EmbeddingModel from the langchain4j-easy-rag dependency through SPI. Each TextSegment is converted into an Embedding using the EmbeddingModel.

note
We have chosen bge-small-en-v1.5 as the default embedding model for Easy RAG. It has achieved an impressive score on the MTEB leaderboard, and its quantized version occupies only 24 megabytes of space. Therefore, we can easily load it into memory and run it in the same process using ONNX Runtime.

Yes, that's right, you can convert text into embeddings entirely offline, without any external services, in the same JVM process. LangChain4j offers 5 popular embedding models out-of-the-box.

All TextSegment-Embedding pairs are stored in the EmbeddingStore.
The last step is to create an AI Service that will serve as our API to the LLM:
interface Assistant {

    String chat(String userMessage);
}

ChatModel chatModel = OpenAiChatModel.builder()
.apiKey(System.getenv("OPENAI_API_KEY"))
.modelName(GPT_4_O_MINI)
.build();

Assistant assistant = AiServices.builder(Assistant.class)
.chatModel(chatModel)
.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
.contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
.build();

Here, we configure the Assistant to use an OpenAI LLM to answer user questions, remember the 10 latest messages in the conversation, and retrieve relevant content from an EmbeddingStore that contains our documents.

And now we are ready to chat with it!
String answer = assistant.chat("How to do Easy RAG with LangChain4j?");

Core RAG APIs
LangChain4j offers a rich set of APIs to make it easy for you to build custom RAG pipelines, ranging from simple ones to advanced ones. In this section, we will cover the main domain classes and APIs.

Document
A Document class represents an entire document, such as a single PDF file or a web page. At the moment, the Document can only represent textual information, but future updates will enable it to support images and tables as well.

Useful methods
Metadata
Each Document contains Metadata. It stores meta information about the Document, such as its name, source, last update date, owner, or any other relevant details.

The Metadata is stored as a key-value map, where the key is of the String type, and the value can be one of the following types: String, Integer, Long, Float, Double.

Metadata is useful for several reasons:

When including the content of the Document in a prompt to the LLM, metadata entries can also be included, providing the LLM with additional information to consider. For example, providing the Document name and source can help improve the LLM's understanding of the content.
When searching for relevant content to include in the prompt, one can filter by Metadata entries. For example, you can narrow down a semantic search to only Documents belonging to a specific owner.
When the source of the Document is updated (for example, a specific page of documentation), one can easily locate the corresponding Document by its metadata entry (for example, "id", "source", etc.) and update it in the EmbeddingStore as well to keep it in sync.
Useful methods
Document Loader
You can create a Document from a String, but a simpler method is to use one of our document loaders included in the library:

FileSystemDocumentLoader from the langchain4j module
ClassPathDocumentLoader from the langchain4j module
UrlDocumentLoader from the langchain4j module
AmazonS3DocumentLoader from the langchain4j-document-loader-amazon-s3 module
AzureBlobStorageDocumentLoader from the langchain4j-document-loader-azure-storage-blob module
GitHubDocumentLoader from the langchain4j-document-loader-github module
GoogleCloudStorageDocumentLoader from the langchain4j-document-loader-google-cloud-storage module
SeleniumDocumentLoader from the langchain4j-document-loader-selenium module
TencentCosDocumentLoader from the langchain4j-document-loader-tencent-cos module
Document Parser
Documents can represent files in various formats, such as PDF, DOC, TXT, etc. To parse each of these formats, there's a DocumentParser interface with several implementations included in the library:

TextDocumentParser from the langchain4j module, which can parse files in plain text format (e.g. TXT, HTML, MD, etc.)
ApachePdfBoxDocumentParser from the langchain4j-document-parser-apache-pdfbox module, which can parse PDF files
ApachePoiDocumentParser from the langchain4j-document-parser-apache-poi module, which can parse MS Office file formats (e.g. DOC, DOCX, PPT, PPTX, XLS, XLSX, etc.)
ApacheTikaDocumentParser from the langchain4j-document-parser-apache-tika module, which can automatically detect and parse almost all existing file formats
Here is an example of how to load one or multiple Documents from the file system:

// Load a single document
Document document = FileSystemDocumentLoader.loadDocument("/home/langchain4j/file.txt", new TextDocumentParser());

// Load all documents from a directory
List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j", new TextDocumentParser());

// Load all *.txt documents from a directory
PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j", pathMatcher, new TextDocumentParser());

// Load all documents from a directory and its subdirectories
List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively("/home/langchain4j", new TextDocumentParser());


You can also load documents without explicitly specifying a DocumentParser. In this case, a default DocumentParser will be used. The default one is loaded through SPI (e.g. from langchain4j-document-parser-apache-tika or langchain4j-easy-rag, if one of them is imported). If no DocumentParsers are found through SPI, a TextDocumentParser is used as a fallback.

Document Transformer
DocumentTransformer implementations can perform a variety of document transformations such as:

Cleaning: This involves removing unnecessary noise from the Document's text, which can save tokens and reduce distractions.
Filtering: to completely exclude particular Documents from the search.
Enriching: Additional information can be added to Documents to potentially enhance search results.
Summarizing: The Document can be summarized, and its short summary can be stored in the Metadata to be later included in each TextSegment (which we will cover below) to potentially improve the search.
Etc.
Metadata entries can also be added, modified, or removed at this stage.

Currently, the only implementation provided out-of-the-box is HtmlToTextDocumentTransformer in the langchain4j-document-transformer-jsoup module, which can extract desired text content and metadata entries from the raw HTML.

Since there is no one-size-fits-all solution, we recommend implementing your own DocumentTransformer, tailored to your unique data.

Text Segment
Once your Documents are loaded, it is time to split (chunk) them into smaller segments (pieces). LangChain4j's domain model includes a TextSegment class that represents a segment of a Document. As the name suggests, TextSegment can represent only textual information.

To split or not to split?
Useful methods
Document Splitter
LangChain4j has a DocumentSplitter interface with several out-of-the-box implementations:

DocumentByParagraphSplitter
DocumentByLineSplitter
DocumentBySentenceSplitter
DocumentByWordSplitter
DocumentByCharacterSplitter
DocumentByRegexSplitter
Recursive: DocumentSplitters.recursive(...)
They all work as follows:

You instantiate a DocumentSplitter, specifying the desired size of TextSegments and, optionally, an overlap in characters or tokens.
You call the split(Document) or splitAll(List<Document>) methods of the DocumentSplitter.
The DocumentSplitter splits the given Documents into smaller units, the nature of which varies with the splitter. For instance, DocumentByParagraphSplitter divides a document into paragraphs (defined by two or more consecutive newline characters), while DocumentBySentenceSplitter uses the OpenNLP library's sentence detector to split a document into sentences, and so on.
The DocumentSplitter then combines these smaller units (paragraphs, sentences, words, etc.) into TextSegments, attempting to include as many units as possible in a single TextSegment without exceeding the limit set in step 1. If some of the units are still too large to fit into a TextSegment, it calls a sub-splitter. This is another DocumentSplitter capable of splitting units that do not fit into more granular units. All Metadata entries are copied from the Document to each TextSegment. A unique metadata entry "index" is added to each text segment. The first TextSegment will contain index=0, the second index=1, and so on.
Text Segment Transformer
TextSegmentTransformer is similar to DocumentTransformer (described above), but it transforms TextSegments.

As with the DocumentTransformer, there is no one-size-fits-all solution, so we recommend implementing your own TextSegmentTransformer, tailored to your unique data.

One technique that works quite well for improving retrieval is to include the Document title or a short summary in each TextSegment.

Embedding
The Embedding class encapsulates a numerical vector that represents the "semantic meaning" of the content that has been embedded (usually text, such as a TextSegment).

Read more about vector embeddings here:

https://www.elastic.co/what-is/vector-embedding
https://www.pinecone.io/learn/vector-embeddings/
https://cloud.google.com/blog/topics/developers-practitioners/meet-ais-multitool-vector-embeddings
Useful methods
Embedding Model
The EmbeddingModel interface represents a special type of model that converts text into an Embedding.

Currently supported embedding models can be found here.

Useful methods
Embedding Store
The EmbeddingStore interface represents a store for Embeddings, also known as vector database. It allows for the storage and efficient search of similar (close in the embedding space) Embeddings.

Currently supported embedding stores can be found here.

EmbeddingStore can store Embeddings alone or together with the corresponding TextSegment:

It can store only Embedding, by ID. Original embedded data can be stored elsewhere and correlated using the ID.
It can store both Embedding and the original data that has been embedded (usually TextSegment).
Useful methods
EmbeddingSearchRequest
The EmbeddingSearchRequest represents a request to search in an EmbeddingStore. It has the following attributes:

Embedding queryEmbedding: The embedding used as a reference.
int maxResults: The maximum number of results to return. This is an optional parameter. Default: 3.
double minScore: The minimum score, ranging from 0 to 1 (inclusive). Only embeddings with a score >= minScore will be returned. This is an optional parameter. Default: 0.
Filter filter: The filter to be applied to the Metadata during search. Only TextSegments whose Metadata matches the Filter will be returned.
Filter
The Filter allows filtering by Metadata entries when performing a vector search.

Currently, the following Filter types/operations are supported:

IsEqualTo
IsNotEqualTo
IsGreaterThan
IsGreaterThanOrEqualTo
IsLessThan
IsLessThanOrEqualTo
IsIn
IsNotIn
ContainsString
And
Not
Or
note
Not all embedding stores support filtering by Metadata, please see the "Filtering by Metadata" column here.

Some stores that support filtering by Metadata do not support all possible Filter types/operations. For example, ContainsString is currently supported only by Milvus, PgVector and Qdrant.

More details about Filter can be found here.

EmbeddingSearchResult
The EmbeddingSearchResult represents a result of a search in an EmbeddingStore. It contains the list of EmbeddingMatches.

Embedding Match
The EmbeddingMatch represents a matched Embedding along with its relevance score, ID, and original embedded data (usually TextSegment).

Embedding Store Ingestor
The EmbeddingStoreIngestor represents an ingestion pipeline and is responsible for ingesting Documents into an EmbeddingStore.

In the simplest configuration, EmbeddingStoreIngestor embeds provided Documents using a specified EmbeddingModel and stores them, along with their Embeddings in a specified EmbeddingStore:

EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
.embeddingModel(embeddingModel)
.embeddingStore(embeddingStore)
.build();

ingestor.ingest(document1);
ingestor.ingest(document2, document3);
IngestionResult ingestionResult = ingestor.ingest(List.of(document4, document5, document6));

All ingest() methods in EmbeddingStoreIngestor return an IngestionResult. The IngestionResult contains useful information, including TokenUsage, which shows how many tokens were used for embedding.

Optionally, the EmbeddingStoreIngestor can transform Documents using a specified DocumentTransformer. This can be useful if you want to clean, enrich, or format Documents before embedding them.

Optionally, the EmbeddingStoreIngestor can split Documents into TextSegments using a specified DocumentSplitter. This can be useful if Documents are big, and you want to split them into smaller TextSegments to improve the quality of similarity searches and reduce the size and cost of a prompt sent to the LLM.

Optionally, the EmbeddingStoreIngestor can transform TextSegments using a specified TextSegmentTransformer. This can be useful if you want to clean, enrich, or format TextSegments before embedding them.

An example:

EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()

    // adding userId metadata entry to each Document to be able to filter by it later
    .documentTransformer(document -> {
        document.metadata().put("userId", "12345");
        return document;
    })

    // splitting each Document into TextSegments of 1000 tokens each, with a 200-token overlap
    .documentSplitter(DocumentSplitters.recursive(1000, 200, new OpenAiTokenCountEstimator("gpt-4o-mini")))

    // adding a name of the Document to each TextSegment to improve the quality of search
    .textSegmentTransformer(textSegment -> TextSegment.from(
            textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
            textSegment.metadata()
    ))

    .embeddingModel(embeddingModel)
    .embeddingStore(embeddingStore)
    .build();

Naive RAG
Once our documents are ingested (see previous sections), we can create an EmbeddingStoreContentRetriever to enable naive RAG functionality.

When using AI Services, naive RAG can be configured as follows:

ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
.embeddingStore(embeddingStore)
.embeddingModel(embeddingModel)
.maxResults(5)
.minScore(0.75)
.build();

Assistant assistant = AiServices.builder(Assistant.class)
.chatModel(model)
.contentRetriever(contentRetriever)
.build();

Naive RAG Example

Advanced RAG
Advanced RAG can be implemented with LangChain4j with the following core components:

QueryTransformer
QueryRouter
ContentRetriever
ContentAggregator
ContentInjector
The following diagram shows how these components work together:

The process is as follows:

The user produces a UserMessage, which is converted into a Query
The QueryTransformer transforms the Query into one or multiple Querys
Each Query is routed by the QueryRouter to one or more ContentRetrievers
Each ContentRetriever retrieves relevant Contents for each Query
The ContentAggregator combines all retrieved Contents into a single final ranked list
This list of Contents is injected into the original UserMessage
Finally, the UserMessage, containing the original query along with the injected relevant content, is sent to the LLM
Please refer to the Javadoc of each component for more details.

Retrieval Augmentor
RetrievalAugmentor is an entry point into the RAG pipeline. It is responsible for augmenting a ChatMessage with relevant Contents retrieved from various sources.

An instance of a RetrievalAugmentor can be specified during the creation of an AI Service:

Assistant assistant = AiServices.builder(Assistant.class)
...
.retrievalAugmentor(retrievalAugmentor)
.build();

Every time an AI Service is invoked, the specified RetrievalAugmentor will be called to augment the current UserMessage.

You can use the default implementation of a RetrievalAugmentor (described below) or implement a custom one.

Default Retrieval Augmentor
LangChain4j provides an out-of-the-box implementation of the RetrievalAugmentor interface: DefaultRetrievalAugmentor, which should be suitable for the majority of RAG use cases. It was inspired by this article and this paper. It is recommended to review these resources for a better understanding of the concept.

Query
Query represents a user query in the RAG pipeline. It contains the text of the query and query metadata.

Query Metadata
The Metadata inside the Query contains information that might be useful in various components of the RAG pipeline, for example:

Metadata.userMessage() - the original UserMessage that should be augmented
Metadata.chatMemoryId() - the value of a @MemoryId-annotated method parameter. More details here. This can be used to identify the user and apply access restrictions or filters during the retrieval.
Metadata.chatMemory() - all previous ChatMessages. This can help to understand the context in which the Query was asked.
Query Transformer
QueryTransformer transforms the given Query into one or multiple Querys. The goal is to enhance retrieval quality by modifying or expanding the original Query.

Some known approaches to improve retrieval include:

Query compression
Query expansion
Query re-writing
Step-back prompting
Hypothetical document embeddings (HyDE)
More details can be found here.

Default Query Transformer
DefaultQueryTransformer is the default implementation used in DefaultRetrievalAugmentor. It does not make any modifications to the Query, it just passes it through.

Compressing Query Transformer
CompressingQueryTransformer uses an LLM to compress the given Query and previous conversation into a standalone Query. This is useful when the user might ask follow-up questions that refer to information in previous questions or answers.

Here is an example:

User: Tell me about John Doe
AI: John Doe was a ...
User: Where did he live?

The query Where did he live? by itself would not be able to retrieve the needed information because there is no explicit reference to John Doe, making it unclear who he refers to.

When using CompressingQueryTransformer, the LLM will read the entire conversation and transform Where did he live? into Where did John Doe live?.

Expanding Query Transformer
ExpandingQueryTransformer uses an LLM to expand the given Query into multiple Querys. This is useful because LLM can rephrase and reformulate Query in various ways, which will help to retrieve more relevant content.

Content
Content represents the content relevant to the user Query. Currently, it is limited to text content (i.e., TextSegment), but in the future it may support other modalities (e.g., images, audio, video, etc.).

Content Retriever
ContentRetriever retrieves Contents from an underlying data source using a given Query. The underlying data source can be virtually anything:

Embedding store
Full-text search engine
Hybrid of vector and full-text search
Web Search Engine
Knowledge graph
SQL database
etc.
The list of Content returned by ContentRetriever is ordered by relevance, from highest to lowest.

Embedding Store Content Retriever
EmbeddingStoreContentRetriever retrieves relevant Content from the EmbeddingStore using the EmbeddingModel to embed the Query.

Here is an example:

EmbeddingStore embeddingStore = ...
EmbeddingModel embeddingModel = ...

ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
.embeddingStore(embeddingStore)
.embeddingModel(embeddingModel)
.maxResults(3)
// maxResults can also be specified dynamically depending on the query
.dynamicMaxResults(query -> 3)
.minScore(0.75)
// minScore can also be specified dynamically depending on the query
.dynamicMinScore(query -> 0.75)
.filter(metadataKey("userId").isEqualTo("12345"))
// filter can also be specified dynamically depending on the query
.dynamicFilter(query -> {
String userId = getUserId(query.metadata().chatMemoryId());
return metadataKey("userId").isEqualTo(userId);
})
.build();

Web Search Content Retriever
WebSearchContentRetriever retrieves relevant Content from the web using a WebSearchEngine.

All supported WebSearchEngine integrations can be found here.

Here is an example:

WebSearchEngine googleSearchEngine = GoogleCustomWebSearchEngine.builder()
.apiKey(System.getenv("GOOGLE_API_KEY"))
.csi(System.getenv("GOOGLE_SEARCH_ENGINE_ID"))
.build();

ContentRetriever contentRetriever = WebSearchContentRetriever.builder()
.webSearchEngine(googleSearchEngine)
.maxResults(3)
.build();

Complete example can be found here.

SQL Database Content Retriever
SqlDatabaseContentRetriever is an experimental implementation of the ContentRetriever that can be found in the langchain4j-experimental-sql module.

It uses the DataSource and an LLM to generate and execute SQL queries for given natural language Query.

See javadoc of the SqlDatabaseContentRetriever for more information.

Here is an example.

Azure AI Search Content Retriever
AzureAiSearchContentRetriever is an integration with Azure AI Search. It supports full-text, vector, and hybrid search, as well as re-ranking. It can be found in the langchain4j-azure-ai-search module. Please refer to the AzureAiSearchContentRetriever Javadoc for more information.

Neo4j Content Retriever
Neo4jContentRetriever is an integration with the Neo4j graph database. It converts natural language queries into Neo4j Cypher queries and retrieves relevant information by running these queries in Neo4j. It can be found in the langchain4j-community-neo4j-retriever module.

Query Router
QueryRouter is responsible for routing Query to the appropriate ContentRetriever(s).

Default Query Router
DefaultQueryRouter is the default implementation used in DefaultRetrievalAugmentor. It routes each Query to all configured ContentRetrievers.

Language Model Query Router
LanguageModelQueryRouter uses the LLM to decide where to route the given Query.

Content Aggregator
The ContentAggregator is responsible for aggregating multiple ranked lists of Content from:

multiple Querys
multiple ContentRetrievers
both
Default Content Aggregator
The DefaultContentAggregator is the default implementation of ContentAggregator, which performs two-stage Reciprocal Rank Fusion (RRF). Please see DefaultContentAggregator Javadoc for more details.

Re-Ranking Content Aggregator
The ReRankingContentAggregator uses a ScoringModel, like Cohere, to perform re-ranking. The complete list of supported scoring (re-ranking) models can be found here. Please see ReRankingContentAggregator Javadoc for more details.

Content Injector
ContentInjector is responsible for injecting of Contents returned by ContentAggregator into the UserMessage.

Default Content Injector
DefaultContentInjector is the default implementation of ContentInjector that simply appends Contents to the end of a UserMessage with the prefix Answer using the following information:.

You can customize how Contents are injected into the UserMessage in 3 ways:

Override the default PromptTemplate:
RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
.contentInjector(DefaultContentInjector.builder()
.promptTemplate(PromptTemplate.from("{{userMessage}}\n{{contents}}"))
.build())
.build();

Please note that PromptTemplate must contain {{userMessage}} and {{contents}} variables.

Extend DefaultContentInjector and override one of the format methods
Implement a custom ContentInjector
DefaultContentInjector also supports injecting Metadata entries from retrieved Content.textSegment():

DefaultContentInjector.builder()
.metadataKeysToInclude(List.of("source"))
.build()

In this case, TextSegment.text() will be prepended with the "content: " prefix, and each value from Metadata will be prepended with a key. The final UserMessage will look like this:

How can I cancel my reservation?

Answer using the following information:
content: To cancel a reservation, go to ...
source: ./cancellation_procedure.html

content: Cancellation is allowed for ...
source: ./cancellation_policy.html

Parallelization
When there is only a single Query and a single ContentRetriever, DefaultRetrievalAugmentor performs query routing and content retrieval in the same thread. Otherwise, an Executor is used to parallelize the processing. By default, a modified (keepAliveTime is 1 second instead of 60 seconds) Executors.newCachedThreadPool() is used, but you can provide a custom Executor instance when creating the DefaultRetrievalAugmentor:

DefaultRetrievalAugmentor.builder()
...
.executor(executor)
.build;

Accessing Sources
If you wish to access the sources (retrieved Contents used to augment the message) when using AI Services, you can easily do so by wrapping the return type in the Result class:

interface Assistant {

    Result<String> chat(String userMessage);
}

Result<String> result = assistant.chat("How to do Easy RAG with LangChain4j?");

String answer = result.content();
List<Content> sources = result.sources();

When streaming, a Consumer<List<Content>> can be specified using the onRetrieved() method:

interface Assistant {

    TokenStream chat(String userMessage);
}

assistant.chat("How to do Easy RAG with LangChain4j?")
.onRetrieved((List<Content> sources) -> ...)
.onPartialResponse(...)
.onCompleteResponse(...)
.onError(...)
.start();

Examples
Easy RAG
Naive RAG
Advanced RAG with Query Compression
Advanced RAG with Query Routing
Advanced RAG with Re-Ranking
Advanced RAG with Including Metadata
Advanced RAG with Metadata Filtering
Advanced RAG with multiple Retrievers
Advanced RAG with Web Search
Advanced RAG with SQL Database
Skipping Retrieval
RAG + Tools
Loading Documents