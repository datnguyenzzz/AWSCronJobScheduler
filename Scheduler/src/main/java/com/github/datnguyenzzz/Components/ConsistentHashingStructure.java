package com.github.datnguyenzzz.Components;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @apiNote Consistent hashing data structure
 */
@Component
@Scope("singleton")
public class ConsistentHashingStructure {
    
    //proper DS to store hash code for CH is TreeSet 
    private TreeSet<Integer> engineHashingCircle;
    private Map<Integer, SchedulerEngine> engineHashMap;

    @PostConstruct
    public void init() {
        this.engineHashingCircle = new TreeSet<>();
        this.engineHashMap = new HashMap<>();
    }

    /**
     * 
     * @param engine ScheduleEngine
     * @apiNote Add new Schedule engine into structure
     */
    public void addNewEngine(SchedulerEngine engine) {

    };
}
