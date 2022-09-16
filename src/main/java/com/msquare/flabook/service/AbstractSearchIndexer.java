package com.msquare.flabook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CacheMode;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSearchIndexer {

    public abstract Class<?> getClazz();

    public abstract EntityManager getEntityManager();

    private final AtomicBoolean indexing = new AtomicBoolean(false);

    @Transactional
    public void index() {
        log.info("start indexer");
        if (indexing.get()) {
            log.info("duplicate index process");
            return;
        }
        indexing.set(true);
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(getEntityManager());
        FullTextSession session = fullTextEntityManager.unwrap(FullTextSession.class);
        try {
            session
                    .createIndexer(getClazz())
                    .typesToIndexInParallel(1)
                    .batchSizeToLoadObjects(5)
                    .purgeAllOnStart(false)
                    .cacheMode(CacheMode.NORMAL)
                    .threadsToLoadObjects(5)
                    .idFetchSize(10)
                    .progressMonitor(new MassIndexerProgressMonitor() {
                        @Override
                        public void documentsBuilt(int i) {
                            log.trace("documentsBuilt : {}", i);
                        }

                        @Override
                        public void entitiesLoaded(int i) {
                            log.trace("entitiesLoaded : {}", i);
                        }

                        @Override
                        public void addToTotalCount(long l) {
                            log.trace("addToTotalCount : {}", l);
                        }

                        @Override
                        public void indexingCompleted() {
                            indexing.set(false);
                            log.info("indexingCompleted");
                        }

                        @Override
                        public void documentsAdded(long l) {
                            log.trace("documentsAdded : {}", l);
                        }
                    })
                    .startAndWait();
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            indexing.set(false);
            log.error("index", e);
        }

        log.info("start end");
    }

}
