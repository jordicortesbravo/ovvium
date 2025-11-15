package com.ovvium.services.util.common.domain;

import lombok.*;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ovvium.services.util.common.domain.adapters.PageableAdapter;

import java.io.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlJavaTypeAdapter(PageableAdapter.class)
public class FileContent implements Serializable{

    private static final long serialVersionUID = 49900566177723543L;

    public static final int BUFFER_SIZE = 16384;

    private String name;
    private byte[] data;

    @SneakyThrows
    public void setData(InputStream is) {
        @Cleanup val os = new ByteArrayOutputStream();
        write(is, os);
        data = os.toByteArray();
    }

    @SneakyThrows
    public InputStream toStream() {
        return new ByteArrayInputStream(data);
    }

    @SneakyThrows
    private void write(InputStream is, OutputStream os) {
        int nRead;
        byte[] data = new byte[BUFFER_SIZE];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            os.write(data, 0, nRead);
        }
        os.flush();
    }
}
