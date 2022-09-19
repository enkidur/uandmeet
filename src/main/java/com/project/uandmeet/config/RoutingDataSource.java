//package com.project.uandmeet.config;
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import static com.project.uandmeet.config.DatabaseType.*;
//
//
//public class RoutingDataSource extends AbstractRoutingDataSource {
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? REPLICA : SOURCE;
//    }
//}
