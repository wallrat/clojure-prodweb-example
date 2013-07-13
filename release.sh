readonly ARTIFACT=myapp

echo
echo "Packing resources"
mkdir -p resources
rm -f resources/public.zip
zip -r resources/public.zip public

echo
echo "storing GIT SHA1"
GIT_SHA1=`(git show-ref --head --hash=8 2> /dev/null || echo 00000000) | head -n1`
echo -n $GIT_SHA1 > ./resources/git-sha1

echo
echo "creating uberjar"
LEIN_SNAPSHOTS_IN_RELEASE=y lein uberjar

echo "creating artifact '$ARTIFACT'"
cat stub.sh > $ARTIFACT
cat target/myapp-*-standalone.jar >> $ARTIFACT
chmod +x $ARTIFACT

echo "done"
