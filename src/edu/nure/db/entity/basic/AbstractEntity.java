package edu.nure.db.entity.basic;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * Created by bod on 02.12.15.
 */
public abstract class AbstractEntity implements Transmittable {
    public static final int ID_NOT_SET = -1;

    @Override
    public String toXML() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ObjectOutputStream bout = new ObjectOutputStream(out)) {

            String tagName = getClass().getSimpleName().toLowerCase();
            bout.writeObject(this);
            return "<" + tagName + " code=\"" + Base64.encodeBase64String(out.toByteArray()) + "\"/>";
        } catch (IOException ex) {
            return "";
        }
    }

    public static <T> T fromXml(String code, Class<T> tClass) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(code));
             ObjectInputStream in = new ObjectInputStream(inputStream);) {
            Object ob = in.readObject();
            return tClass.cast(ob);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new Exception("Undefined class");
        }
    }

    public static Transmittable fromXml(String code) throws Exception {
        return fromXml(code, Transmittable.class);
    }
}
