readonly ARTIFACT=myapp

echo
echo "Packing resources"
mkdir -p resources
rm -f resources/public.zip
zip -r resources/public.zip public

echo
echo "creating uberjar"
LEIN_SNAPSHOTS_IN_RELEASE=y lein uberjar

echo "creating artifact '$ARTIFACT'"
cat stub.sh > $ARTIFACT
cat target/myapp-*-standalone.jar >> $ARTIFACT
chmod +x $ARTIFACT

echo "done"
