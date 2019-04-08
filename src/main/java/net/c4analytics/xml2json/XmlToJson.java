package net.c4analytics.xml2json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.xml2json.utils.Util;


@EventDriven
@SideEffectFree
@Tags({"XML Json"})
@InputRequirement(Requirement.INPUT_REQUIRED)
@CapabilityDescription("Convert FlowFile XML content to JSON")
public class XmlToJson extends AbstractProcessor {
	final static Logger logger = LoggerFactory.getLogger(XmlToJson.class);

    private List<PropertyDescriptor> properties;
    private Set<Relationship> relationships;
    
	public static final Relationship SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Success")
            .build();
    
    public static final Relationship FAILURE = new Relationship.Builder()
            .name("failure")
            .description("Failure")
            .build();

    
    @Override
    public void init(final ProcessorInitializationContext context){
        List<PropertyDescriptor> properties = new ArrayList<>();
        this.properties = Collections.unmodifiableList(properties);
        Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships.add(FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
    }
    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
    	FlowFile flowfile = session.get();
        final AtomicReference<byte[]> ac = new AtomicReference<>();
        try {
	        session.read(flowfile, new InputStreamCallback() { 
	            @Override
	            public void process(InputStream in) throws IOException {
	                try{
	                	ac.set(IOUtils.toByteArray(in));
	                } catch(Exception ex){
	                	logger.error(Util.stackTrace(ex));
	                    throw new IOException("Failed to read FlowFile Contents");
	                }
	                
	            }
	        });
			JSONObject jo = XML.toJSONObject(new String(ac.get(),"UTF-8"));
	        if(jo != null){
	        	flowfile = session.write(flowfile, new OutputStreamCallback() {
	                @Override
	                public void process(OutputStream out) throws IOException {
	                    out.write(jo.toString(2).getBytes("UTF-8"));
	                }
	            });
	        }
	        session.transfer(flowfile, SUCCESS);   
        } catch (Exception e) {
        	logger.error(Util.stackTrace(e));
	        session.transfer(flowfile, FAILURE);   
        }
     	session.commit();
    }
    
    @Override
    public Set<Relationship> getRelationships(){
        return relationships;
    }
    
    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors(){
        return properties;
    }
    
}