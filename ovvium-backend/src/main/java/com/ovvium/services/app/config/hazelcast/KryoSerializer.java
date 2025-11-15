package com.ovvium.services.app.config.hazelcast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Slf4j
@RequiredArgsConstructor
public class KryoSerializer<T> implements StreamSerializer<T> {

	private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(KryoSerializer::configureKryo);

	private final int id;
	@Getter
	private final Class<T> concreteClazz;
	private final boolean compress = true;

	@Override
	public int getTypeId() {
		return id;
	}

	@Override
	public void write(ObjectDataOutput objectDataOutput, T obj)
			throws IOException {
		Kryo kryo = kryoThreadLocal.get();

		if (compress) {
			val byteArrayOutputStream = new ByteArrayOutputStream(16384);
			DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
			Output output = new Output(deflaterOutputStream);
			kryo.writeObject(output, obj);
			output.close();

			byte[] bytes = byteArrayOutputStream.toByteArray();
			objectDataOutput.write(bytes);
		} else {
			Output output = new Output((OutputStream) objectDataOutput);
			kryo.writeObject(output, obj);
			output.flush();
		}
		log.debug("Serialized object " + concreteClazz.getName());
	}

	@Override
	public T read(ObjectDataInput objectDataInput) throws IOException {
		InputStream in = (InputStream) objectDataInput;
		if (compress) {
			in = new InflaterInputStream(in);
		}
		Input input = new Input(in);
		Kryo kryo = kryoThreadLocal.get();
		log.debug("Deserialized object " + concreteClazz.getName());
		return kryo.readObject(input, concreteClazz);
	}

	@Override
	public void destroy() {
	}

	private static Kryo configureKryo() {
		Kryo kryo = new Kryo();
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.setRegistrationRequired(false);
		return kryo;
	}
}
