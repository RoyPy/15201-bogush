package ru.nsu.ccfit.bogush.chat.serialization;

import org.junit.Test;
import ru.nsu.ccfit.bogush.chat.LoggingConfiguration;
import ru.nsu.ccfit.bogush.chat.User;
import ru.nsu.ccfit.bogush.chat.message.Message;
import ru.nsu.ccfit.bogush.chat.message.MessageFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class XMLMessageSerializerTest {
	static { LoggingConfiguration.setConfigFileToDefaultIfNotSet(); }

	private static final String FILE_NAME = ".test";
	private static final Path FILE_PATH = Paths.get(FILE_NAME);

	static private Serializer<Message> serializer;
	static {
		try {
			if (Files.notExists(FILE_PATH)) {
				Path path = Files.createFile(FILE_PATH);
				System.out.println("file " + path + " created");
			}
			File file = new File(FILE_NAME);
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(file);
			serializer = MessageSerializerFactory.createSerializer(in, out, Serializer.Type.XML_SERIALIZER);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private Message serializeDeserialize(Message m) throws Serializer.SerializerException {
		serializer.serialize(m);
		return serializer.deserialize();
	}

	@Test
	public void testLoginRequest() throws Serializer.SerializerException {
		Message m = MessageFactory.createLoginRequest("Sergey");
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testUserListRequest() throws Serializer.SerializerException {
		Message m = MessageFactory.createUserListRequest(1968);
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testLogoutEvent() throws Serializer.SerializerException {
		Message m = MessageFactory.createLogoutEvent("Stanislav");
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testErrorMessage() throws Serializer.SerializerException {
		Message m = MessageFactory.createErrorMessage("This is tough");
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testSuccess() throws Serializer.SerializerException {
		Message m = MessageFactory.createSuccess();
		serializeDeserialize(m);
	}

	@Test
	public void testLoginEvent() throws Serializer.SerializerException {
		Message m = MessageFactory.createLoginEvent("Lev");
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testLogoutRequest() throws Serializer.SerializerException {
		Message m = MessageFactory.createLogoutRequest(1921);
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testLoginSuccess() throws Serializer.SerializerException {
		Message m = MessageFactory.createLoginSuccess(1828);
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testUserListSuccess() throws Serializer.SerializerException {
		User[] users = new User[]{
				new User("Lukianenko"),
				new User("Lem"),
				new User("Tolstoy")
		};
		Message m = MessageFactory.createUserListSuccess(users);
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testTextMessageRequest() throws Serializer.SerializerException {
		String text = "Lorem ipsum dolor sit amen";
		Message m = MessageFactory.createTextMessageRequest(text, 1828);
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Test
	public void testTextMessageEvent() throws Serializer.SerializerException {
		String text = "War and peace";
		String author = "Tolstoy";
		Message m = MessageFactory.createTextMessageEvent(text, author);
		Message result = serializeDeserialize(m);
		assertEquals(m, result);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		Files.delete(FILE_PATH);
	}
}