# m-Typer
#### (`Demo branch`) 

m-Typer သည္ [ CMUSphinx Speech Recognition ] (http://cmusphinx.sourceforge.net/wiki/tutorialandroid) စနစ္ကို အသံုးျပဳကာ Myanmar Speech To Text (offline) အျဖစ္ အသံုးျပဳႏိုင္ေစရန္ ရည္ရြယ္ေသာ Android app project တစ္ခု ျဖစ္ပါသည္။

###  [ CMUSphinx Speech Recognition ] (http://cmusphinx.sourceforge.net/wiki/tutorialandroid) စနစ္ အသံုးျပဳရန္ ဘာေတြ လိုအပ္ ပါသလဲ? 

- A Dictionary 
  - စကားလံုး ႏွင့္ အသံထြက္ တြဲေရးထားသည္႔ dictionary
- A Language Model 
  - အထက္ပါ dictionary ကို Recognition စနစ္က သိေစရန္ ေလ႔က်င့္ (train) ေပးထားေသာ Language Model

အခု Demo app တြင္ ျမန္မာဘာသာအတြက္ Language Model မရွိေသးေသာေၾကာင့္ မူရင္းပါ English Language Model ကို အစားသံုး သရုပ္ျပထားျခင္း ျဖစ္ပါသည္။ 

### [ CMUSphinx Speech Recognition ] (http://cmusphinx.sourceforge.net/wiki/tutorialandroid) စနစ္တြင္ dictionary မွ စကားလံုးမ်ားကို recognize သိေစရန္- 

- Recognize သိေစခ်င္ေသာ စကားလံုး keyword မ်ားကို သိေအာင္ လုပ္ျခင္း (keyword search)
- [JSpeech Grammar Format (JSGF)] (‎https://www.w3.org/TR/jsgf/) ျဖင့္ လိုခ်င္ေသာ စကားလံုး အထားအသိုမ်ား တည္ေဆာက္ျပီး သိေအာင္လုပ္ျခင္း (grammar search) 
- ngram/lm search ဟု ေခၚေသာ Dictionary တစ္ခုလံုးမွ စကားလံုးမ်ားကို lm (Language Model) ကို အသံုးျပဳ၍ သိေအာင္ လုပ္ျခင္း 

 ဟူ၍ အမ်ိဳးမ်ိဳး recognize သိေအာင္ လုပ္ႏိုင္ေသာ စနစ္မ်ား ရွိပါသည္။

ယခု Demo app တြင္ ဒုတိယနည္း ျဖစ္ေသာ grammar search ျဖင့္ သရုပ္ျပထားျခင္း ျဖစ္ပါသည္။


( ဆက္လက္ ေရးသားေနဆဲ...) 
