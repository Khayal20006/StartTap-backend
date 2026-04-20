package com.bmu1093a.quill.vacancy.service;

import com.bmu1093a.quill.auth.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserLookupService {
    User getCurrentUser();
}
