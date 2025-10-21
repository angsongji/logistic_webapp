package com.uteexpress.service.customer;

import com.uteexpress.entity.User;

public interface CustomerService {
    User getByUsername(String username);
}
