package com.wanted.teamV.service.impl;

import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

}
