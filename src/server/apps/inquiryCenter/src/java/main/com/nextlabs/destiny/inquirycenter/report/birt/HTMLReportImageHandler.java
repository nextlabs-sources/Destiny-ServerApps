/*
 * Created on Apr 22, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.HTMLCompleteImageHandler;
import org.eclipse.birt.report.engine.api.HTMLRenderContext;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.RenderOptionBase;
import org.eclipse.birt.report.engine.util.FileUtil;


/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/birt/HTMLReportImageHandler.java#1 $
 */

public class HTMLReportImageHandler extends HTMLCompleteImageHandler {

    private static HashMap map = new HashMap( );
    
    protected String handleImage( IImage image, Object context, String prefix,
            boolean needMap )
    {
        String mapID = null;
        if ( needMap )
        {
            mapID = getImageMapID( image );
            if ( map.containsKey( mapID ) )
            {
                return (String) map.get( mapID );
            }
        }

        String imageDirectory = null;
        if ( context != null && ( context instanceof HTMLRenderContext ) )
        {
            HTMLRenderContext myContext = (HTMLRenderContext) context;
            imageDirectory = myContext.getImageDirectory( );
        }
        if ( imageDirectory == null )
        {
            IReportRunnable runnable = image.getReportRunnable( );
            if ( runnable != null )
            {
                IReportEngine engine = runnable.getReportEngine( );
                if ( engine != null )
                {
                    EngineConfig config = engine.getConfig( );
                    if ( config != null )
                    {
                        imageDirectory = config.getTempDir( );
                    }
                }
            }
        }
        if ( imageDirectory == null )
        {
            imageDirectory = System.getProperty( "java.io.tmpdir" );
        }
        if ( imageDirectory == null )
        {
            imageDirectory = ".";
        }
        String outputFile = (String) image.getRenderOption( )
                .getOutputSetting( ).get( RenderOptionBase.OUTPUT_FILE_NAME );

        boolean returnRelativePath = needRelativePath( outputFile,
                imageDirectory );
        String imageOutputDirectory = getImageOutputDirectory( outputFile,
                imageDirectory );
        File file = saveImage( image, prefix, imageOutputDirectory );
        String outputPath = getOutputPath( returnRelativePath, imageDirectory,
                file );
        if ( needMap )
        {
            map.put( mapID, outputPath );
        }
        return outputPath;
    }
    
    private String getOutputPath( boolean needRelativePath,
            String imageDirectory, File outputFile )
    {
        String result = null;
//        if ( needRelativePath )
//        {
//            result = imageDirectory + File.separator + outputFile.getName( );
//        }
//        else
//        {
//            try
//            {
//                result = outputFile.toURL( ).toExternalForm( );
//            }
//            catch ( Exception ex )
//            {
//                result = outputFile.getAbsolutePath( );
//            }
//        }
        result = "/reporter/dashboard?file=" + outputFile.getName();
        return result;
    }
    
    private File saveImage( IImage image, String prefix,
            String imageOutputDirectory )
    {
        File file;
        synchronized ( HTMLCompleteImageHandler.class )
        {
            file = createUniqueFile( imageOutputDirectory, prefix, image
                    .getExtension( ) );
            try
            {
                image.writeImage( file );
            }
            catch ( IOException e )
            {
                log.log( Level.SEVERE, e.getMessage( ), e );
            }
        }
        return file;
    }
    
    private boolean needRelativePath( String reportOutputFile,
            String imageDirectory )
    {
        if ( reportOutputFile == null )
        {
            return false;
        }
        if ( !FileUtil.isRelativePath( imageDirectory ) )
        {
            return false;
        }
        return true;
    }

    private String getImageOutputDirectory( String reportOutputFile,
            String imageDirectory )
    {
        if ( !FileUtil.isRelativePath( imageDirectory ) )
        {
            return imageDirectory;
        }

        String reportOutputDirectory;
        if ( reportOutputFile == null )
        {
            reportOutputDirectory = getTempFile( );
        }
        else
        {
            reportOutputDirectory = new File( reportOutputFile )
                    .getAbsoluteFile( ).getParent( );
        }

        return reportOutputDirectory + File.separator + imageDirectory;
    }
    
    private String getTempFile( )
    {
        return new File( "." ).getAbsolutePath( );
    }
}
