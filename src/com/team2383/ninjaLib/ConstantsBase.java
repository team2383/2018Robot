package com.team2383.ninjaLib;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * ConstantsBase (thanks 254)
 * 
 * Base class for storing robot constants. Anything stored as a public static
 * field will be reflected and be able to set externally
 * 
 * adapted to use the NetworkTables preferences object for editing preferences
 */
public abstract class ConstantsBase {
    public class UnsupportedTypeException extends Exception {
    	public UnsupportedTypeException(String message) {
    		super(message);
    	}
	}

	HashMap<String, Boolean> modifiedKeys = new HashMap<String, Boolean>();
    NetworkTable prefTable;

    public abstract String getFileLocation();
    
    public ConstantsBase() {
    	prefTable = NetworkTableInstance.getDefault().getTable("Preferences");
    	prefTable.getEntry(".type").setString("RobotPreferences");
    	prefTable.addEntryListener(new TableEntryListener() {

			@Override
			public void valueChanged(NetworkTable table, String key, NetworkTableEntry entry, NetworkTableValue value,
					int flags) {
				NetworkTableType type = entry.getType();
				System.out.println("Change in " + key + " detected: " + type.toString());
				switch (type) {
					case kBoolean: 
						setConstant(key, entry.getBoolean(false));
						saveChangesToFile();
						break;
					case kDouble: 
						Double d = entry.getDouble(0);
						if (d % 1 == 0) {
							setConstant(key, d.intValue());
						} else {
							setConstant(key, d);
						}
						saveChangesToFile();
						break;
				}
				
			}
    		
    	}, EntryListenerFlags.kUpdate);
    	
    	this.init();
    }

    public static class Constant {
        public String name;
        public Class<?> type;
        public Object value;

        public Constant(String name, Class<?> type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            String itsName = ((Constant) o).name;
            Class<?> itsType = ((Constant) o).type;
            Object itsValue = ((Constant) o).value;
            return o instanceof Constant && this.name.equals(itsName) && this.type.equals(itsType)
                    && this.value.equals(itsValue);
        }
    }

    public File getFile() {
        String filePath = getFileLocation();
        filePath = filePath.replaceFirst("^~", System.getProperty("user.home"));
        return new File(filePath);
    }

    public boolean setConstant(String name, Double value) {
        return setConstantRaw(name, value);
    }

    public boolean setConstant(String name, Integer value) {
        return setConstantRaw(name, value);
    }

    public boolean setConstant(String name, String value) {
        return setConstantRaw(name, value);
    }
    
    public boolean setConstant(String name, Boolean value) {
        return setConstantRaw(name, value);
    }

    private boolean setConstantRaw(String name, Object value) {
        boolean success = false;
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                try {
                    Object current = field.get(this);
                    field.set(this, value);
                    System.out.println("field now set to: " + field.get(this));
                    
                    addToNetworkTables(field, value);
                    
                    success = true;
                    if (!value.equals(current)) {
                        modifiedKeys.put(name, true);
                    }
                } catch (IllegalArgumentException | IllegalAccessException | UnsupportedTypeException e) {
                    System.out.println("Could not set field: " + name);
                }
            }
        }
        return success;
    }
    
    private void addToNetworkTables(Field field, Object value) throws UnsupportedTypeException {
    	Class<?> type = field.getType();
		NetworkTableEntry entry = prefTable.getEntry(field.getName());
    	if (type.equals(Boolean.TYPE)) {
    		entry.setBoolean((boolean) value);
    	} else if (type.equals(Double.TYPE) || type.equals(Integer.TYPE)) {
    		entry.setNumber((Number) value);
    	} else {
     		throw new UnsupportedTypeException("FATAL: Type " + type.getCanonicalName() + " is not supported!");
     	}
    }

    public Object getValueForConstant(String name) throws Exception {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                try {
                    return field.get(this);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new Exception("Constant not found");
                }
            }
        }
        throw new Exception("Constant not found");
    }

    public Constant getConstant(String name) {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                try {
                    return new Constant(field.getName(), field.getType(), field.get(this));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Constant("", Object.class, 0);
    }

    public Collection<Constant> getConstants() {
        List<Constant> constants = (List<Constant>) getAllConstants();
        int stop = constants.size() - 1;
        for (int i = 0; i < constants.size(); ++i) {
            Constant c = constants.get(i);
            if ("kEndEditableArea".equals(c.name)) {
                stop = i;
            }
        }
        return constants.subList(0, stop);
    }

    private Collection<Constant> getAllConstants() {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        List<Constant> constants = new ArrayList<Constant>(declaredFields.length);
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                Constant c;
                try {
                    c = new Constant(field.getName(), field.getType(), field.get(this));
                    constants.add(c);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return constants;
    }

    public JSONObject getJSONObjectFromFile() throws IOException, ParseException {
        File file = getFile();
        if (file == null || !file.exists() || file.length() == 0) {
            return new JSONObject();
        }
        FileReader reader;
        reader = new FileReader(file);
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(reader);
    }
    
    public void init() {
    	Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
					addToNetworkTables(field, field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException | UnsupportedTypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        createFile();
        loadFromFile();
    }

    public void loadFromFile() {
        try {
            JSONObject jsonObject = getJSONObjectFromFile();
            Set<?> keys = jsonObject.keySet();
            for (Object o : keys) {
                String key = (String) o;
                Object value = jsonObject.get(o);
                if (value instanceof Long && getConstant(key).type.equals(int.class)) {
                    value = new BigDecimal((Long) value).intValueExact();
                }
                setConstantRaw(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void createFile() {
        File file = getFile();
    	try {
            boolean wasCreated = file.createNewFile();
            System.out.println("ff " + wasCreated + " L: " + file.length());
            if (wasCreated || file.length() == 0) {
            	JSONObject json = getJSONObjectFromFile();
                FileWriter writer = new FileWriter(file);
                Field[] declaredFields = this.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    try {
                        Object value = field.get(this);
                        json.put(field.getName(), value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                writer.write(json.toJSONString());
                writer.close();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    
    public void saveChangesToFile() {
        File file = getFile();
        if (file == null) {
            return;
        }
        try {
            JSONObject json = getJSONObjectFromFile();
            FileWriter writer = new FileWriter(file);
            for (String key : modifiedKeys.keySet()) {
                try {
                    Object value = getValueForConstant(key);
                    json.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            writer.write(json.toJSONString());
            writer.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
