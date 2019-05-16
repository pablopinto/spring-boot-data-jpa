package com.estudiando.springboot.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.estudiando.springboot.app.models.entity.Cliente;


public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {

	
}
