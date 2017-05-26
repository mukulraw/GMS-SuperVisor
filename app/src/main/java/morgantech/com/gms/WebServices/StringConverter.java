package morgantech.com.gms.WebServices;

/**
 * Created by Administrator on 09-01-2017.
 */

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Scanner;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

/**
 * Simple response body to String converter
 */
public class StringConverter implements Converter {

    @Override
    public Object fromBody( TypedInput body, Type type ) throws ConversionException {

        if ( !type.equals( String.class ) ) {
            throw new IllegalArgumentException( "Unsupported type, only String is supported" );
        }

        Scanner scanner = null;

        try {
            scanner = new Scanner( body.in() ).useDelimiter( "\\A" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        final String result = scanner.hasNext() ? scanner.next() : "";

        close( body );

        return result;
    }

    @Override
    public TypedOutput toBody( Object object ) {
        return new TypedString( String.valueOf( object ) );
    }

    private void close( TypedInput body ) {

        try {
            body.in().close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
