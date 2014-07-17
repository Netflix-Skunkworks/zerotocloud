export CATALINA_OPTS=" \
    -verbose:sizes \
    -Xmx2560m -Xms512m \
    -XX:MaxPermSize=128m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:-UseGCOverheadLimit \
    -XX:+ExplicitGCInvokesConcurrent \
    -XX:+PrintGCDateStamps -XX:+PrintGCDetails \
    -XX:+PrintTenuringDistribution \
    -XX:+CMSClassUnloadingEnabled \
    -XX:+UseConcMarkSweepGC \
    "
