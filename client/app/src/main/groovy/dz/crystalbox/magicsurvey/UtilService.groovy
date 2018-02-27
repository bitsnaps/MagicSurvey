package dz.crystalbox.magicsurvey

import groovy.transform.CompileStatic

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@CompileStatic
class UtilService {

    def zipString(String s){
        def targetStream = new ByteArrayOutputStream()
        def zipStream = new GZIPOutputStream(targetStream)
        zipStream.write(s.getBytes('UTF-8'))
        zipStream.close()
        def zipped = targetStream.toByteArray()
        targetStream.close()
        zipped.encodeBase64()
    }

    String unzipString(String compressed){
        def inflaterStream = new GZIPInputStream(new ByteArrayInputStream(compressed.decodeBase64()))
        inflaterStream.getText('UTF-8')
    }

}
