# folio-deceso

This repository contains code and data used to measure excess mortality in Mexico City in the context of the COVID-19 epidemic. This information was published as a series of articles in the magazine [Nexos](https://nexos.com.mx/). 

Different commits correspond to the different articles as follows:
- [¿Qué nos dicen las actas de defunción de la CDMX?](https://datos.nexos.com.mx/?p=1388) published on May 26, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/dfa238c1df8a8763d265f419e5a659555031036a)


- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 31 de mayo de 2020:](https://datos.nexos.com.mx/?p=1443) published on June 6, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/b79e23732280e4c64f8cb38091c52bc8776c1610)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 7 de junio 2020 y seguimiento semanal](https://datos.nexos.com.mx/?p=1458) published on June 15, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/8a1a1470aeb9d2957a93843dbd3ce075998e14ca)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 28 de junio 2020](https://datos.nexos.com.mx/?p=1480) published on July 3, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/f5bc8c3fa79593a65e84e972218ba7ba4ef5e191)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 5 de julio 2020](https://datos.nexos.com.mx/?p=1498) published on July 13, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/5bd5189880f92dac0c59ec02fdc46f4725f2de79)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 12 de julio 2020](https://datos.nexos.com.mx/?p=1540) published on July 20, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/e5814b44126d235abcb1cbc3bc734367dc54efe4)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 19 de julio 2020](https://datos.nexos.com.mx/?p=1550) published on July 27, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/3c59e94527020636f37eb16210ebd6639db5dd61)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 26 de julio 2020](https://datos.nexos.com.mx/?p=1594) published on August 6, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/abcf6440de582f74d3671b5ce2a147f3d962e293)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 2 de agosto 2020](https://datos.nexos.com.mx/?p=1616) published on August 13, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/0e5022079ae8ee0190da23c53821fe17981d65af)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 9 de agosto 2020](https://datos.nexos.com.mx/?p=1634) published on August 20, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/3980c07926f5e48ab1149cdbd6daf57f7dc0137c)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 23 de agosto 2020](https://datos.nexos.com.mx/?p=1657) published on September 3, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/0879dca7a268df6a44f9e4c7ce4a65b41bf844f9)

- [¿Qué nos dicen las actas de defunción de la CDMX? Actualización al 20 de septiembre 2020](https://datos.nexos.com.mx/?p=1715) published on October 12, 2020 -> [commit](https://github.com/mariorz/folio-deceso/tree/5483eae673aacfa376e6e079ac26794ac3d41664)


# Source data
The data for __2019__ and __2020__ consist of a count of deaths certificates registered in the Civil Registry Tribunals of Mexico City. 

To obtain this data, we developed a program to determine the total number of death certificates available for consultation on the [web platform of the Civil Registry Tribunals of Mexico City](http://www.rcivil.cdmx.gob.mx/solicitudactas/busqueda/registrales/clase_acta/DEFUNCION).  Each death certificate is indexed according to the year of registry, the tribunal in which it was registered, and a unique ID number.  These IDs are consecutive and they reset at the start of the year for each tribunal, meaning that the first death registered in every tribunal each year has an ID of “1”, the second one has an ID of “2”, and so on until registering the last death certificate for the period. Our program ran a binary search for each of the 52 tribunals to determine the total number of certificated registered for a specific time period. After the publication of the first article mentioned above a CAPTCHA was added to the site, which prevented us from using our program to obtain subsequent updates. We have since transitioned to obtaining further updates by performing the binary searches manually.

For years prior to __2018__, the information is available on the [National Institute of Statistics, Geography and Informatics (INEGI)](https://www.inegi.org.mx/programas/mortalidad/default.html#Datos_abiertos) webpage.  *Note: The day of registry of the death certificates is not specified for a limited number of cases and “99” appears instead.  To adjust for it, we uniformly distributed the total number of deaths reported with a “99” day within their respective months.*


# File description

### Weekly updates
Weeks used for the following files correspond to the ISO week system. Each week starting on a Monday and closing on a Sunday.

* [`inegi-days.csv`](https://github.com/mariorz/folio-deceso/blob/master/resources/inegi-days.csv)	: Number of daily deaths and acumulated deaths registered every day of the period 2016 - 2018 using information from the National Institute of Statistics, Geography and Informatics (INEGI).
* [`weeks.csv`](https://github.com/mariorz/folio-deceso/blob/master/resources/weeks.csv) : Number of weekly deaths registered in 2016, 2017, 2018 from INEGI, and total death certificate counts for 2019 and 2020 using our methodology. 

### First publication - Monthly data
* [`months2019.csv`](resources/months2019.csv) : Highest death certificate ID number by tribunal for each month in 2019.
* [`months2020.csv`](resources/months2020.csv) : Highest death certificate ID number by tribunal for each month in 2020.

In order to verify our methodology, we drew three random samples of ID numbers for the three largest tribunals, which account for over 75% of the registered certificates, to confirm that they actually existed and that the date of deaths occurred within the expected period.  The samples for Tribunal 14, 18, and 51 are available here: [`Sample-juzgado14.csv`](https://github.com/mariorz/folio-deceso/blob/master/resources/sample-juzgado14.csv) , [`Sample-juzgado18.csv`](https://github.com/mariorz/folio-deceso/blob/master/resources/sample-juzgado18.csv) , [`Sample-juzgado51.csv`](https://github.com/mariorz/folio-deceso/blob/master/resources/sample-juzgado51.csv) .

As an additional sanity check, we counted the total number of registered certificates for each tribunal in 2016, 2017 and 2018 and compared those numbers against the officially published mortality data for those years. In all three cases, our methodology returned results higher than the official data, but all were below 0.6%, within our acceptable margin of error. The results of our certificate count for each of these years are in [`cy2016.edn`](https://github.com/mariorz/folio-deceso/blob/master/resources/cy2016.edn), [`cy2017.edn`](https://github.com/mariorz/folio-deceso/blob/master/resources/cy2017.edn) and [`cy2018.edn`](https://github.com/mariorz/folio-deceso/blob/master/resources/cy2018.edn) files.

# License
The data collected and analyzed is available under the [Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/).

The data and files that we have generated from official sources are freely available for public use, as long as we are cited cited as a source.

How to cite? --- Romero Zavala, Mario & Despeghel, Laurianne. (2020). *Excess mortality data for Mexico City 2020.* https://github.com/mariorz/folio-deceso

# Authors
This data has been collected and analyzed by [Mario Romero Zavala](https://twitter.com/mariorz) and Laurianne Despeghel. 


If you have any questions or suggestions, please email mario@romero.fm or lauriannedsp@gmail.com 
