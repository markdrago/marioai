package ch.idsia.ai.agents;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 8:28:06 PM
 * Package: ch.idsia.ai.agents
 */
public class AgentsPool
{
    public static void addAgent(Agent agent) {
        agentsHashMap.put(agent.getName(), agent);
    }

    public static Collection<Agent> getAgentsCollection()
    {
        return agentsHashMap.values();
    }
    
    static HashMap<String, Agent> agentsHashMap = new LinkedHashMap<String, Agent>();
}
