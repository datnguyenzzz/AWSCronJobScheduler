package com.github.datnguyenzzz.Interfaces;

import com.github.datnguyenzzz.dto.AWSJob;

public interface AWSPublisher {
    void publish(AWSJob awsJob);
    void publish(String content);
}
