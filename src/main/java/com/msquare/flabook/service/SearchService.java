package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Slf4j
@Service
@AllArgsConstructor
public class SearchService {
    @SuppressWarnings("unused")
    private final EntityManager em;
}
