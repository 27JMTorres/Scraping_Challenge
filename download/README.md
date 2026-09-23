# README #
Technical challenge

# INSTALLATION #

1. Please, run "docker build -t wmtest ." inside the main directory to build the
    docker image required to run this project.
2. Once the image has been created, please run "docker run --restart unless-stopped --privileged -d --name=test 
    -p=8080:8080 -e JAVA_OPTS="-Xms2560M -Xmx3072M -Dfile.encoding=UTF8 -Duser.timezone=Mexico/General" wmtest:latest" 
    to up the container.
3. To verify the logs from the apllication, please run "docker logs -f test"


# How does it work? #

1. Once the application has started, please type "http://localhost:8080/category/" to get 
    all the categories from walmart.
    **PLEASE NOTE that this project just reach the second level category,
    In case you need more, please contact the administrator.
2. To verify the item download process, please type "http://localhost:8080/product?category=/lacteos/yogurt/yogurt-bebible"
    **PLEASE NOTE that this project only works with paramas such as
    "/lacteos/yogurt/yogurt-bebible" -> "https://super.walmart.com.mx/browse/lacteos/yogurt/yogurt-bebible/120006_120103_120367"
    "/lacteos/huevo/blanco" -> https://super.walmart.com.mx/browse/lacteos/huevo/blanco/120006_120093_120355
    "/bebidas-y-licores/cervezas/clara" -> "https://super.walmart.com.mx/browse/bebidas-y-licores/cervezas/clara/3680051_120094_120431"
    These strings are part of the main url's from super.walmart but this project
    only works with this kind of expressions.
    ***Please, use the format as in left side.
    ***To reduce issues, you can have the examples then
    1. http://localhost:8080/product?category=/lacteos/yogurt/yogurt-bebible
    2. http://localhost:8080/product?category=/lacteos/huevo/blanco
    3. http://localhost:8080/product?category=/bebidas-y-licores/cervezas/clara
3. Probably, you will note that some other examples won´t work, but don´t worry
    this site has some inconsistencies in the name of every category name.