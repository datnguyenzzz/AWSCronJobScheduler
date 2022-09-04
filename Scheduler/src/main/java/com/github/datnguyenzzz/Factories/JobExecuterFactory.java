package com.github.datnguyenzzz.Factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.JobExecuter;

/** 
 * @implSpec Currently have bean:
 * - awsSQSPublisher
 * - awsKinesisPubliser
 * - awsS3Publisher
 * 
 * implement interface AWSPublisher
*/
@Component()
@Scope("singleton")
public class JobExecuterFactory{

    private final String PUBLISHER = "Executer";
    private final String AWS = "aws";
    
    @Autowired
    private ApplicationContext ctx;

    /**
     * 
     * @param usedService
     * @return Bean correctsponding to used AWS service
     */
    public JobExecuter getObject(String usedService) {
        JobExecuter targetBean = ctx.getBean(getTargetBeanName(usedService), JobExecuter.class);
        return targetBean;
    }

    private String getTargetBeanName(String usedService) {
        StringBuilder sb = new StringBuilder();
        sb.append(AWS);
        sb.append(usedService);
        sb.append(PUBLISHER);

        return sb.toString();
    }

}
