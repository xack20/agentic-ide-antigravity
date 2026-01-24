package com.ecommerce.shared.common.queries;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for processing a specific query type.
 *
 * @param <TQuery>    The query type this handler processes
 * @param <TResponse> The response type returned after processing
 */
public interface QueryHandler<TQuery extends Query<TResponse>, TResponse> {

    /**
     * Handle the query and return the result.
     *
     * @param query The query to process
     * @return A CompletableFuture containing the result
     */
    CompletableFuture<TResponse> handle(TQuery query);

    /**
     * Get the class type of query this handler processes.
     */
    Class<TQuery> getQueryType();
}
