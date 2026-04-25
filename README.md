# Fake-Cloudinary
Microservicio desarrollado en Spring Boot para remplazar el uso de Cloudinary, 
tiene las funcionalidades de subir archivos pdf, extraer el texto de los mismos y 
generar imágenes a partir de sus páginas.

Un solo archivo:
```
curl -X POST -F "file=@Linux Device Drivers.3rd.Edition.pdf" http://localhost:8080/pdf/upload
```

Multiples archivos:
```
curl -X POST -F "files=@PATH/FILE.pdf" -F "files=@PATH/FILE.pdf" http://localhost:8080/pdf/upload-multiple
```

El proceso de subir un archivo PDF mostrará información relativa al mismo, asi como enlaces HATEOAS (Hypermedia as the Engine of Application State) para 
realizar las operaciones extraer el texto y generar imágenes a partir de las páginas: 
```
{
    "uuid": "0c20a11d-30c2-471c-a978-011220d2290a",
    "originalName": "Linux Device Drivers.3rd.Edition.pdf",
    "fileSize": 12825921,
    "contentType": "application/pdf",
    "totalPages": 630,
    "deleted": false,
    "uploadedAt": "2026-04-22T04:53:57.89754972",
    "metadata": {
        "title": null,
        "author": null,
        "subject": null,
        "keywords": null,
        "creator": "1.00 - www.pdftk.com",
        "producer": "itext-paulo (lowagie.com)[JDK1.1] - build 132",
        "creationDate": "2005-03-25",
        "modificationDate": "2005-03-25",
        "metadataKeys": [
            "CreationDate",
            "Producer",
            "Creator",
            "ModDate"
        ],
        "trapped": null,
        "dimensions": [
            {
                "width": 612.0,
                "height": 792.0,
                "orientation": "VERTICAL"
            }
        ],
        "fonts": [
            "CDBADJ+Times-Roman",
            "PJBKCO+Symbol",
            "MAGFHI+TheSansMonoCondensed-SemiLightItalic",
            "CHLLNI+TheSansMonoCondensed-SemiLightItalic",
            "PLKBJH+Birka-Italic",
            "PEKDLM+TheSansMonoCondensed-Bold",
            "PCLGPB+Myriad-CnBoldItalic",
            "PJBFNN+Myriad-Condensed",
            "PJBFIM+Myriad-CnItalic",
            "ANIBKF+TheSansMonoCondensed-Bold",
            "PJAHKM+Times-Roman",
            "AEGKJF+Birka",
            "PEJMJI+TheSansMonoCondensed-SemiLight",
            "CHLLOJ+TheSansMonoCondensed-Bold",
            "OGPNBA+Birka-Italic",
            "PJAGLM+Birka",
            "AEGKIN+Myriad-CnSemibold",
            "AEHIKJ+TheSansMonoCondensed-Bold",
            "IEACOJ+TheSansMonoCondensed-Bold",
            "AEGNJK+TheSansMonoCondensed-SemiLight",
            "AEGPOE+Myriad-CnBold",
            "IDPHIF+TheSansMonoCondensed-SemiLight",
            "PLKKAL+TheSansMonoCondensed-SemiLightItalic",
            "AJNAOC+Times-Roman",
            "PLKDCE+Times-Roman",
            "CDBAAN+Myriad-CnSemibold",
            "AEGKEE+Birka-Italic",
            "IHOHNC+Times-Roman",
            "PJBFMO+Helvetica-Condensed-Black",
            "JLBOAD+Birka-Italic",
            "DJMGCA+Birka",
            "ODJNJD+Times-Roman",
            "MAFHEC+Times-Roman",
            "PHHPFJ+Times-Roman",
            "ANHHOH+Times-Roman",
            "OHAAMO+TheSansMonoCondensed-Bold",
            "IMCGLO+Birka-Italic",
            "PHHOEH+Myriad-CnSemibold",
            "IDPGOP+Birka",
            "DJMFND+Birka-Italic",
            "MAGALE+TheSansMonoCondensed-SemiLight",
            "AEHABF+Myriad-CnItalic",
            "IMDOMO+Myriad-Condensed",
            "DIPFON+Birka-Italic",
            "ANHGLM+Birka",
            "IMEIAP+Myriad-CnBold",
            "IMCHAN+Birka",
            "AJNHII+Myriad-CnBold",
            "MAFKCO+Myriad-Condensed",
            "PJAJOC+TheSansMonoCondensed-SemiLight",
            "DJMOFC+TheSansMonoCondensed-SemiLight",
            "HPPDFA+Myriad-CnItalic",
            "HPPIOO+TheSansMonoCondensed-SemiLight",
            "CDBANE+Birka",
            "CFPMFM+Myriad-CnSemibold",
            "ONHBEL+TheSansMonoCondensed-Bold",
            "AJMPKK+Birka",
            "ANHGGM+Birka-Italic",
            "AEHIPJ+TheSansMonoCondensed-SemiLightItalic",
            "CHLLKN+TheSansMonoCondensed-SemiLight",
            "PCIFPL+Times-Roman",
            "PLKBNO+Myriad-CnSemibold",
            "IDPGOI+Myriad-CnSemibold",
            "IMCIEC+Times-Roman",
            "PEJLJK+Birka-Italic",
            "AJMPFJ+Birka-Italic",
            "CDBHMA+Myriad-Condensed",
            "PHHPDA+TheSansMonoCondensed-SemiLight",
            "DIPGDG+Myriad-CnSemibold",
            "PLKBOG+Birka",
            "IEACKG+Myriad-CnItalic",
            "DJMHEM+Times-Roman",
            "ODJPNP+TheSansMonoCondensed-SemiLightItalic",
            "ONGLNC+Birka-Italic",
            "PCLGJH+TheSansMonoCondensed-Bold",
            "CFPMJA+Birka",
            "ODKLHE+Myriad-Condensed",
            "PCLGKD+TheSansMonoCondensed-Plain",
            "PJAGHG+Birka-Italic",
            "PJAGLF+Myriad-CnSemibold",
            "PJBFPE+Myriad-CnBold",
            "JLBOEJ+Birka",
            "DJOIMA+TheSansMonoCondensed-Bold",
            "OHBCJD+Myriad-CnBold",
            "CDBHGB+Birka-SemiBoldItalic",
            "CFPMLP+Birka-Italic",
            "PJBPFG+TheSansMonoCondensed-Bold",
            "ODKLLJ+Helvetica-Condensed-Bold",
            "OPDMOD+TheSansMonoCondensed-SemiLight",
            "PEJLOF+Birka",
            "PEJMMP+Times-Roman",
            "CHLFLM+Times-Roman",
            "DJMJLO+Myriad-Condensed",
            "CHLEAJ+Myriad-CnSemibold",
            "ONGMAM+Myriad-CnSemibold",
            "OGPOHN+Times-Roman",
            "AJNDEG+TheSansMonoCondensed-SemiLight",
            "AEGLKN+Times-Roman",
            "IHOHEH+Myriad-CnSemibold",
            "CGAADO+Times-Roman",
            "HPOKJA+Birka-Italic",
            "PHHNPN+Birka-Italic",
            "ANHIOF+TheSansMonoCondensed-SemiLight",
            "ODJPPB+TheSansMonoCondensed-Bold",
            "JLBOEC+Myriad-CnSemibold",
            "HPOKNI+Myriad-CnSemibold",
            "PLKEAE+TheSansMonoCondensed-SemiLight",
            "JLBPAG+TheSansMonoCondensed-SemiLight",
            "HPOKNP+Birka",
            "OPDMBN+Times-Roman",
            "AEGPPJ+Myriad-Condensed",
            "IMDPBL+Myriad-CnItalic",
            "DJMGBJ+Myriad-CnSemibold",
            "HPPDHO+Myriad-CnBold",
            "IMEICE+TheSansMonoCondensed-Bold",
            "ODKCCA+Myriad-CnBoldItalic",
            "CHLDMB+Birka-Italic",
            "ONGNBE+Times-Roman",
            "OPEPOM+Myriad-Condensed",
            "PCIEKP+Birka",
            "ONGMON+TheSansMonoCondensed-SemiLight",
            "ODJMDM+Myriad-CnSemibold",
            "ODJLPC+Birka-Italic",
            "AJNLKN+TheSansMonoCondensed-Bold",
            "ODKLFL+Myriad-CnItalic",
            "OPDLCG+Myriad-CnSemibold",
            "IHOHIK+Birka",
            "OGPPMA+TheSansMonoCondensed-SemiLight",
            "IDPGKA+Birka-Italic",
            "OPDKOG+Birka-Italic",
            "PHHOEO+Birka",
            "JLDCAK+Myriad-Condensed",
            "IMCHAG+Myriad-CnSemibold",
            "JLBPEF+Times-Roman",
            "PEJLNN+Myriad-CnSemibold",
            "OGPNGB+Myriad-CnSemibold",
            "DIPGDO+Birka",
            "DJNGOI+Myriad-CnItalic",
            "MAFFMC+Birka",
            "AJMPKC+Myriad-CnSemibold",
            "ANHGLE+Myriad-CnSemibold",
            "IMEIDE+TheSansMonoCondensed-Plain",
            "MAFFHC+Birka-Italic",
            "IEACJB+Myriad-CnBold",
            "MAFFLK+Myriad-CnSemibold",
            "IMEIEB+ConstantWillison-Bold",
            "ODJOLH+TheSansMonoCondensed-SemiLight",
            "CFPNGJ+Myriad-Condensed",
            "IMDLDN+TheSansMonoCondensed-SemiLightItalic",
            "IDPHMI+Times-Roman",
            "PCIEGI+Birka-Italic",
            "PCLGMB+Myriad-CnItalic",
            "IHOHHH+Birka-SC",
            "PLKHJL+TheSansMonoCondensed-Bold",
            "HPOLPL+Times-Roman",
            "OGPNGJ+Birka",
            "OPDLCN+Birka",
            "AJNHND+Myriad-CnItalic",
            "OHADOH+Myriad-Condensed",
            "PCIEKI+Myriad-CnSemibold",
            "IMCKDM+TheSansMonoCondensed-SemiLight",
            "CDBAPC+Birka-Italic",
            "CHLFAM+Birka",
            "OHBCKC+TheSansMonoCondensed-Plain",
            "PCIFOC+TheSansMonoCondensed-SemiLight",
            "ODKCFN+Myriad-CnBold",
            "DIPGKO+Times-Roman",
            "ODJMEE+Birka",
            "ONGMBE+Birka",
            "ANHJMP+Myriad-Condensed"
        ]
    },
    "_links": {
        "download": {
            "href": "http://localhost:8080/downloads/0c20a11d-30c2-471c-a978-011220d2290a"
        },
        "thumbnail": {
            "href": "http://localhost:8080/images/0c20a11d-30c2-471c-a978-011220d2290a/1"
        },
        "extractText": {
            "href": "http://localhost:8080/pdf/0c20a11d-30c2-471c-a978-011220d2290a/text"
        }
    }
}
```

## Comandos docker

ejecutar maven clean && install

docker build -t fake-cloudinary:1.0 .

docker run -p 80:8080 fake-cloudinary:1.0 -d

docker exec -ti 1c88589eeadd /bin/ash

docker rmi _hash_ V para remover imágenes

docker rm -f $(docker ps -a -q --filter "status=exited")