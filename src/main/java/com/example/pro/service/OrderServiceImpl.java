package com.example.pro.service;

import com.example.pro.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    // 향후 DB 저장용 메서드를 만들 수도 있음
    @Override
    public void saveOrder(OrderDTO order) {
        // 저장 로직 (예: orderRepository.save(orderEntity))
    }
}
