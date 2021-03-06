/*
Copyright (C) 2001 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS
(http://www.kclee.com/clemens/java/javancss/).

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */

package javancss;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ccl.util.Util;

/**
 * Generates ascii output of Java metrics.
 *
 * @author    Chr. Clemens Lee <clemens@kclee.com>
 *            , Windows 13 10 line feed feature by John Wilson.
 * @version   $Id: AsciiFormatter.java 121 2009-01-17 22:19:45Z hboutemy $
 */
public class AsciiFormatter implements Formatter
{
    private static final int LEN_NR = 3;
    private static final String NL = System.getProperty( "line.separator" );

    private final Javancss _javancss;

    private String[] _header = null;
    private int      _length = 0;
    private int      _nr     = 0;

    private NumberFormat _pNumberFormat = null;

    private String _formatListHeader( int lines, String[] header )
    {
        _header = header;

        _nr = 0;

        StringBuffer sRetVal = new StringBuffer();

        _length = Util.itoa( lines ).length();
        int spaces = Math.max( 0, _length - LEN_NR );
        _length = spaces + LEN_NR;
        sRetVal.append( Util.multiplyChar(' ', spaces) );
        sRetVal.append( "Nr." );
        for( int nr = 0; nr < header.length; nr++ )
        {
            sRetVal.append( ' ' ).append( header[ nr ] );
        }
        sRetVal.append( NL );

        return sRetVal.toString();
    }

    private String _formatListLine( String name, int[] value )
    {
        StringBuffer sLine = new StringBuffer();

        _nr++;
        sLine.append( Util.paddWithSpace( _nr, _length ) );
        for( int index = 0; index < _header.length - 1; index++ )
        {
            sLine.append( ' ' );
            sLine.append( Util.paddWithSpace( value[ index ]
                                              , _header[ index ].length() ) );
        }
        sLine.append( ' ' );
        sLine.append( name );
        sLine.append( NL );

        return sLine.toString();
    }

    private double _divide( int divident, int divisor )
    {
        double dRetVal = 0.0;
        if ( divisor > 0) {
            dRetVal = Math.round(((double)divident/(double)divisor)*100)/100.0;
        }

        return dRetVal;
    }

    private double _divide( long divident, long divisor )
    {
        double dRetVal = 0.0;
        if ( divisor > 0) {
            dRetVal = Math.round(((double)divident/(double)divisor)*100)/100.0;
        }

        return dRetVal;
    }

    private String _formatPackageMatrix( int packages
                                         , int classesSum
                                         , int functionsSum
                                         , int javadocsSum
                                         , int ncssSum      )
    {
        ((DecimalFormat)_pNumberFormat).applyPattern( "###0.00" );
        int maxItemLength = _pNumberFormat.format(ncssSum).length();
        maxItemLength = Math.max(9, maxItemLength);
        String sRetVal =
            Util.paddWithSpace( "Packages" , maxItemLength ) + ' '
            + Util.paddWithSpace("Classes", maxItemLength) + ' '
            + Util.paddWithSpace("Functions", maxItemLength) + ' '
            + Util.paddWithSpace("NCSS", maxItemLength) + ' '
            + Util.paddWithSpace("Javadocs", maxItemLength)
            + " | per" + NL

            + Util.multiplyChar( '-', (maxItemLength + 1)*6 + 1 ) + NL
            + Util.paddWithSpace(_pNumberFormat.format(packages), maxItemLength) + ' '
            + Util.paddWithSpace(_pNumberFormat.format(classesSum), maxItemLength) + ' '
            + Util.paddWithSpace(_pNumberFormat.format(functionsSum), maxItemLength) + ' '
            + Util.paddWithSpace(_pNumberFormat.format(ncssSum), maxItemLength) + ' '
            + Util.paddWithSpace(_pNumberFormat.format(javadocsSum), maxItemLength)
            + " | Project" + NL

            + Util.multiplyChar( ' ', maxItemLength + 1 )
            + Util.paddWithSpace( _pNumberFormat.format( _divide( classesSum, packages ) ), maxItemLength ) + ' '
            + Util.paddWithSpace( _pNumberFormat.format( _divide( functionsSum, packages ) ), maxItemLength ) + ' '
            + Util.paddWithSpace( _pNumberFormat.format( _divide( ncssSum, packages ) ), maxItemLength ) + ' '
            + Util.paddWithSpace( _pNumberFormat.format( _divide( javadocsSum, packages ) ), maxItemLength )
            + " | Package" + NL

            + Util.multiplyChar( ' ', (maxItemLength + 1)*2 )
            + Util.paddWithSpace( _pNumberFormat.format( _divide( functionsSum, classesSum ) ), maxItemLength ) + ' '
            + Util.paddWithSpace( _pNumberFormat.format( _divide( ncssSum, classesSum ) ), maxItemLength ) + ' '
            + Util.paddWithSpace( _pNumberFormat.format( _divide( javadocsSum, classesSum ) ), maxItemLength )
            + " | Class" + NL

            + Util.multiplyChar( ' ', (maxItemLength + 1)*3 )
            + Util.paddWithSpace( _pNumberFormat.format( _divide( ncssSum, functionsSum ) ), maxItemLength ) + ' '
            + Util.paddWithSpace( _pNumberFormat.format( _divide( javadocsSum, functionsSum ) ), maxItemLength )
            + " | Function" + NL;

        ((DecimalFormat)_pNumberFormat).applyPattern( "#,##0.00" );

        return sRetVal;
    }

    public AsciiFormatter( Javancss javancss )
    {
        super();

        _javancss = javancss;

        _pNumberFormat = NumberFormat.getInstance( Locale.US );
        ((DecimalFormat)_pNumberFormat).applyPattern( "#,##0.00" );
    }

    public String printPackageNcss()
    {
        List vPackageMetrics = _javancss.getPackageMetrics();

        int packages = vPackageMetrics.size();

        StringBuffer sbRetVal = new StringBuffer( _formatListHeader( packages
                                            , new String[] {   "  Classes"
                                                             , "Functions"
                                                             , "     NCSS"
                                                             , " Javadocs"
                                                             , "Package" } ) );

        int classesSum   = 0;
        int functionsSum = 0;
        int javadocsSum  = 0;
        int ncssSum      = 0;
        for( Iterator ePackages = vPackageMetrics.iterator(); ePackages.hasNext(); )
        {
            PackageMetric pPackageMetric = (PackageMetric)ePackages.next();

            classesSum   += pPackageMetric.classes;
            functionsSum += pPackageMetric.functions;
            ncssSum      += pPackageMetric.ncss;
            javadocsSum  += pPackageMetric.javadocs;
            sbRetVal.append( _formatListLine( pPackageMetric.name
                                        , new int[] { pPackageMetric.classes
                                                      , pPackageMetric.functions
                                                      , pPackageMetric.ncss
                                                      , pPackageMetric.javadocs
                                        } ) );
        }

        int packagesLength = Util.itoa( packages ).length();
        int spaces = Math.max( packagesLength, LEN_NR ) + 1;
        sbRetVal.append( Util.multiplyChar(' ', spaces ) +
               "--------- --------- --------- ---------" + NL );

        sbRetVal.append( Util.multiplyChar(' ', spaces )
            + Util.paddWithSpace( classesSum, 9 ) + ' '
            + Util.paddWithSpace( functionsSum, 9 ) + ' '
            + Util.paddWithSpace( ncssSum, 9 ) + ' '
            + Util.paddWithSpace( javadocsSum, 9 )
            + " Total" + NL + NL );

        sbRetVal.append( _formatPackageMatrix( packages
                                         , classesSum
                                         , functionsSum
                                         , javadocsSum
                                         , ncssSum      ) );

        return sbRetVal.toString();
    }

    private String _formatObjectResume( int objects
                                        , long lObjectSum
                                        , long lFunctionSum
                                        , long lClassesSum
                                        , long lJVDCSum     )
    {
        double fAverageNcss     = _divide( lObjectSum  , objects );
        double fAverageFuncs    = _divide( lFunctionSum, objects );
        double fAverageClasses  = _divide( lClassesSum , objects );
        double fAverageJavadocs = _divide( lJVDCSum    , objects );
        String sRetVal = "Average Object NCSS:             "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageNcss),     9) + NL
            + "Average Object Functions:        "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageFuncs),    9) + NL
            + "Average Object Inner Classes:    "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageClasses),  9) + NL
            + "Average Object Javadoc Comments: "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageJavadocs), 9) + NL
            + "Program NCSS:                    "
            + Util.paddWithSpace(_pNumberFormat.format(_javancss.getNcss()), 9) + NL;

        return sRetVal;
    }

    public String printObjectNcss() {
        List/*<ObjectMetric>*/ vObjectMetrics = _javancss.getObjectMetrics();

        StringBuffer sbRetVal = new StringBuffer( _formatListHeader( vObjectMetrics.size()
                                            , new String[] { "NCSS"
                                                             , "Functions"
                                                             , "Classes"
                                                             , "Javadocs"
                                                             , "Class"     } ) );
        long lFunctionSum = 0;
        long lClassesSum  = 0;
        long lObjectSum   = 0;
        long lJVDCSum     = 0;
        for( Iterator eClasses = vObjectMetrics.iterator(); eClasses.hasNext(); )
        {
            ObjectMetric classMetric = (ObjectMetric)eClasses.next();
            String sClass = classMetric.name;
            int objectNcss = classMetric.ncss;
            int functions  = classMetric.functions;
            int classes    = classMetric.classes;
            int jvdcs      = classMetric.javadocs;
            lObjectSum   += (long)objectNcss;
            lFunctionSum += (long)functions;
            lClassesSum  += (long)classes;
            lJVDCSum     += (long)jvdcs;
            sbRetVal.append( _formatListLine( sClass
                                        , new int[] { objectNcss
                                                      , functions
                                                      , classes
                                                      , jvdcs     } ) );
        }

        sbRetVal.append( _formatObjectResume( vObjectMetrics.size()
                                        , lObjectSum
                                        , lFunctionSum
                                        , lClassesSum
                                        , lJVDCSum            ) );

        return sbRetVal.toString();
    }

    private String _formatFunctionResume( int functions
                                          , long lFunctionSum
                                          , long lCCNSum
                                          , long lJVDCSum     )
    {
        double fAverageNcss = _divide( lFunctionSum, functions );
        double fAverageCCN  = _divide( lCCNSum     , functions );
        double fAverageJVDC = _divide( lJVDCSum    , functions );

        String sRetVal = "Average Function NCSS: "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageNcss), 10) + NL
            + "Average Function CCN:  "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageCCN),  10) + NL
            + "Average Function JVDC: "
            + Util.paddWithSpace(_pNumberFormat.format(fAverageJVDC), 10) + NL
            + "Program NCSS:          "
            + Util.paddWithSpace(_pNumberFormat.format(_javancss.getNcss()), 10) + NL;

        return sRetVal;
    }

    public String printFunctionNcss()
    {
        StringBuffer sRetVal = new StringBuffer(80000);

        List vFunctionMetrics = _javancss.getFunctionMetrics();

        sRetVal.append( _formatListHeader( vFunctionMetrics.size()
                                           , new String[] { "NCSS"
                                                            , "CCN"
                                                            , "JVDC"
                                                            , "Function" } ) );

        long lFunctionSum = 0;
        long lCCNSum      = 0;
        long lJVDCSum     = 0;
        for( Iterator eFunctions = vFunctionMetrics.iterator(); eFunctions.hasNext(); )
        {
            FunctionMetric functionMetric = (FunctionMetric)eFunctions.next();
            String sFunction = functionMetric.name;
            int functionNcss = functionMetric.ncss;
            int functionCCN  = functionMetric.ccn;
            int functionJVDC = functionMetric.javadocs;

            lFunctionSum += (long)functionNcss;
            lCCNSum      += (long)functionCCN;
            lJVDCSum     += (long)functionJVDC;
            sRetVal.append( _formatListLine( sFunction
                                             , new int[] { functionNcss
                                                           , functionCCN
                                                           , functionJVDC } ) );
        }

        sRetVal.append( _formatFunctionResume( vFunctionMetrics.size()
                                               , lFunctionSum
                                               , lCCNSum
                                               , lJVDCSum              ) );

        return sRetVal.toString();
    }

    public String printJavaNcss()
    {
        return "Java NCSS: " + _javancss.getNcss() + NL;
    }
}
