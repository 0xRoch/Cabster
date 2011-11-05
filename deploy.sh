if [ ! -d "./modules" ]; then
    ../play/play deps
fi
../play/play gae:deploy --gae=../bin/gae-java/
