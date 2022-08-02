# Migration Version 0.0.x to 0.1.x

## Database

The Product EDC [PostgreSQL Migration Extension](../../edc-extensions/postgresql-migration/README.md) is able to run
normal migrations. But this extension will never drop / delete any data, therefore this must be done by the user itself.

### Manual Fix Contract Definitions

The SelectorExpression of a Contract Definition is serialized as JSON and put into the database. The Criteria schema changed and
the existing entries will cause _NullPointerExceptions_.

<details>
  <summary>Example Exception</summary>

```
[2022-08-02 09:32:37] [SEVERE ] Could not handle multipart request: null
org.eclipse.dataspaceconnector.spi.EdcException
        at org.eclipse.dataspaceconnector.transaction.local.LocalTransactionContext.execute(LocalTransactionContext.java:70)
        at org.eclipse.dataspaceconnector.sql.assetindex.SqlAssetIndex.queryAssets(SqlAssetIndex.java:141)
        at org.eclipse.dataspaceconnector.sql.assetindex.SqlAssetIndex.queryAssets(SqlAssetIndex.java:134)
        at org.eclipse.dataspaceconnector.contract.offer.ContractOfferServiceImpl.lambda$queryContractOffers$2(ContractOfferServiceImpl.java:61)
        at java.base/java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:271)
        at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:177)
        at java.base/java.util.LinkedList$LLSpliterator.forEachRemaining(LinkedList.java:1239)
        at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
        at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
        at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
        at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
        at org.eclipse.dataspaceconnector.ids.core.service.CatalogServiceImpl.getDataCatalog(CatalogServiceImpl.java:55)
        at org.eclipse.dataspaceconnector.ids.core.service.ConnectorServiceImpl.getConnector(ConnectorServiceImpl.java:51)
        at org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandler.handle(ConnectorDescriptionRequestHandler.java:74)
        at org.eclipse.dataspaceconnector.ids.api.multipart.handler.DescriptionHandler.handleRequestInternal(DescriptionHandler.java:117)
        at org.eclipse.dataspaceconnector.ids.api.multipart.handler.DescriptionHandler.handleRequest(DescriptionHandler.java:82)
        at org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartController.request(MultipartController.java:146)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:566)
        at org.glassfish.jersey.server.model.internal.ResourceMethodInvocationHandlerFactory.lambda$static$0(ResourceMethodInvocationHandlerFactory.java:52)
        at org.glassfish.jersey.server.model.internal.AbstractJavaResourceMethodDispatcher$1.run(AbstractJavaResourceMethodDispatcher.java:124)
        at org.glassfish.jersey.server.model.internal.AbstractJavaResourceMethodDispatcher.invoke(AbstractJavaResourceMethodDispatcher.java:167)
        at org.glassfish.jersey.server.model.internal.JavaResourceMethodDispatcherProvider$ResponseOutInvoker.doDispatch(JavaResourceMethodDispatcherProvider.java:176)
        at org.glassfish.jersey.server.model.internal.AbstractJavaResourceMethodDispatcher.dispatch(AbstractJavaResourceMethodDispatcher.java:79)
        at org.glassfish.jersey.server.model.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:475)
        at org.glassfish.jersey.server.model.ResourceMethodInvoker.apply(ResourceMethodInvoker.java:397)
        at org.glassfish.jersey.server.model.ResourceMethodInvoker.apply(ResourceMethodInvoker.java:81)
        at org.glassfish.jersey.server.ServerRuntime$1.run(ServerRuntime.java:255)
        at org.glassfish.jersey.internal.Errors$1.call(Errors.java:248)
        at org.glassfish.jersey.internal.Errors$1.call(Errors.java:244)
        at org.glassfish.jersey.internal.Errors.process(Errors.java:292)
        at org.glassfish.jersey.internal.Errors.process(Errors.java:274)
        at org.glassfish.jersey.internal.Errors.process(Errors.java:244)
        at org.glassfish.jersey.process.internal.RequestScope.runInScope(RequestScope.java:265)
        at org.glassfish.jersey.server.ServerRuntime.process(ServerRuntime.java:234)
        at org.glassfish.jersey.server.ApplicationHandler.handle(ApplicationHandler.java:684)
        at org.glassfish.jersey.servlet.WebComponent.serviceImpl(WebComponent.java:394)
        at org.glassfish.jersey.servlet.WebComponent.service(WebComponent.java:346)
        at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:358)
        at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:311)
        at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:205)
        at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:764)
        at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:508)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:221)
        at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1375)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:176)
        at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:463)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:174)
        at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1297)
        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:129)
        at org.eclipse.jetty.server.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:192)
        at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:122)
        at org.eclipse.jetty.server.Server.handle(Server.java:562)
        at org.eclipse.jetty.server.HttpChannel.lambda$handle$0(HttpChannel.java:505)
        at org.eclipse.jetty.server.HttpChannel.dispatch(HttpChannel.java:762)
        at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:497)
        at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:282)
        at org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:319)
        at org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:100)
        at org.eclipse.jetty.io.SelectableChannelEndPoint$1.run(SelectableChannelEndPoint.java:53)
        at org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy.runTask(AdaptiveExecutionStrategy.java:412)
        at org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy.consumeTask(AdaptiveExecutionStrategy.java:381)
        at org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy.tryProduce(AdaptiveExecutionStrategy.java:268)
        at org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy.produce(AdaptiveExecutionStrategy.java:190)
        at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:894)
        at org.eclipse.jetty.util.thread.QueuedThreadPool$Runner.run(QueuedThreadPool.java:1038)
        at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: java.lang.NullPointerException
        at org.eclipse.dataspaceconnector.sql.translation.SqlConditionExpression.isValidExpression(SqlConditionExpression.java:53)
        at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
        at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1655)
        at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
        at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
        at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
        at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
        at org.eclipse.dataspaceconnector.sql.assetindex.schema.BaseSqlDialectStatements.createQuery(BaseSqlDialectStatements.java:108)
        at org.eclipse.dataspaceconnector.sql.assetindex.SqlAssetIndex.lambda$queryAssets$2(SqlAssetIndex.java:144)
        at org.eclipse.dataspaceconnector.transaction.local.LocalTransactionContext.execute(LocalTransactionContext.java:63)
        ... 69 more
```

</details>


<details>
  <summary>Solution 1: Manually Update all Selector Expression</summary>

    Root of this issue is that the operator, left- and right-operand Criteria field names changed.

    | Old       | New          |
    |:----------|:-------------|
    | left      | operandLeft  |
    | right     | operandRight |
    | op        | operator     |

</details>


<details>
  <summary>Solution 2: Delete All Contract Definitions</summary>

</details>