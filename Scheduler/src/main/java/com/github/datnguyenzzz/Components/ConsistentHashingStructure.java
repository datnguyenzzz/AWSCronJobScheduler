package com.github.datnguyenzzz.Components;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private int numEngines;

    private static final Logger logger = LoggerFactory.getLogger(ConsistentHashingStructure.class);

    @Autowired
    private CronJobConfiguration config;

    @Autowired
    private ApplicationContext ctx;

    @PostConstruct
    public void init() {
        this.engineHashingCircle = new TreeSet<>();
        this.engineHashMap = new HashMap<>();
        this.numEngines = 0;

        // Init config number of engines
        int sz = config.getNumSchedulerEngines();
        for (int i=0; i<sz; i++) {
            SchedulerEngine engine = (SchedulerEngine) ctx.getBean(SchedulerEngine.class);
            logger.info("Init engine - " + engine.getName());
            this.addNewEngine(engine);
        }
    }

    /**
     * 
     * @param engine ScheduleEngine
     * @apiNote Add new Schedule engine into structure
     */
    public void addNewEngine(SchedulerEngine engine) {
        this.numEngines++;
        int engineHashCode = engine.hashCode();
        // hash code must not be collide :-)
        engineHashMap.put(engineHashCode, engine);
        engineHashingCircle.add(engineHashCode);
    };

    /**
     * 
     * @param name
     * @return Appropiate engine for handling with key
     */
    public SchedulerEngine getEngine(String key) {
        int code = key.hashCode();
        int engineCode = 0;
        if (this.engineHashingCircle.ceiling(code) == null) {
            engineCode = this.engineHashingCircle.first();
        }
        else {
            engineCode = this.engineHashingCircle.ceiling(code);
        }

        return this.engineHashMap.get(engineCode);
    }

    public int getNumberOfEngines() {
        return this.numEngines;
    }
}
