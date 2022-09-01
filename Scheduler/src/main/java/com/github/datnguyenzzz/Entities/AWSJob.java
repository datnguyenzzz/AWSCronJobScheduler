package com.github.datnguyenzzz.Entities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class AWSJob extends Object {
    
    @Getter @Setter
    private String name;

    @Getter @Setter
    private String cronTrigger;

    @Getter @Setter
    private List<Message> messages;

    @Getter @Setter
    private String usedService;

    @Getter @Setter
    private String afterJobDone;

    @Getter @Setter
    private String lambdaActionFile;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[\n");
        sb.append("\tname : " + this.name + "\n");
        sb.append("\tcronTrigger : " + this.cronTrigger + "\n");
        sb.append("\tusedService : " + this.usedService + "\n");
        sb.append("\tafterJobDone : " + this.afterJobDone + "\n");
        sb.append("\tlambdaActionFile : " + this.lambdaActionFile + "\n");
        if (messages != null) {
            sb.append("\tmessages: " + "\n");
            for (Message mes: messages) 
                sb.append(mes.toString());
        }

        sb.append("]\n");
        return sb.toString();
    }
}
