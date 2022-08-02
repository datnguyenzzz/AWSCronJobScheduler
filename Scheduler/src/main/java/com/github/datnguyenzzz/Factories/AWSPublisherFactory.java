package com.github.datnguyenzzz.Factories;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.AWSPublisher;

/** 
 * Currently have:
 * - AWSSQSPublisher
 * - AWSKinesisPubliser
 * - AWSS3Publisher
 * 
 * implement interface AWSPublisher
*/
@Component
public class AWSPublisherFactory implements FactoryBean<AWSPublisher> {

    @Override
    public AWSPublisher getObject() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
