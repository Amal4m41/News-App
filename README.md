# News App for Android
A simple news app that uses [NewsApi](https://newsapi.org/) to fetch the latest breaking news and news w.r.t the topic searched by the user. This app was created with the aim to learn the modern android development practices.
<br><br>
<b>Features :</b>
* User can view the latest breaking news
* Search news related to any topic
* Save your favourite news articles so that it can viewed offline later
* Text summarizer to summarize any text (Offline)
* Save, edit summarized texts
* Pagination to only load additional articles if necessary
* Swipe feature to delete saved summaries/articles.
* No internet checking to prevent app crash and alert user about the same

<br><br>
<b>Some optimizations performed :</b>
* Implementing recycler view adapters with DiffUtil : <br>
Normally we pass the list to the Adapter class and then whenever we want to update the list of an adapter we do something like adapter.items = newlist, and then call
adapter.notifyDatasetChanged(), but this is really inefficient as when we call notifyDatasetChanged() the recycler view will always update all of it's items, i.e. even the items that didn't change.
To solve this problem we use DiffUtil as it calculates the difference between two lists and enables us to only update those items that is different, and another advantage is that this happens in the background without blocking our UI/Main thread.

* Placing onClickListener for recycler view items inside view holder class(Considered as best practise) instead of onBindViewHolder() to avoid unnecessary attaching of onClickListener on the views that are recycled by the recyler view.

<br>
<b>Architecture : </b><br>
This app uses the MVVM(Model-View-ViewModel) architecture, and therefore has a unidirectional data flow so that it's easily scalable, flexible, improves testability, separation of concern etc. <br>
<img height="800" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/mvvvm.png"> &nbsp;


<br><br>
<b>Screenshots :</b><br><br>
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/breakingNews.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/searchNews.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/savedNews.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/more.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/articleScreen.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/textSummarizer.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/savedSummary.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/summary.jpg"> &nbsp;
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/editSummary.jpg"> &nbsp;

<br>
Searching and saving/deleting articles : <br><br>
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/searchNewsAndSaved.gif"> <br>

Text summarization and saving/editing/deleting summaries : <br> <br>
<img height="500" src="https://github.com/Amal4m41/News-App/blob/master/screenshots/textSummarization.gif"> <br>

<b>Credits : </b><br>
* [Text2Summary](https://github.com/shubham0204/Text2Summary-Android) library that was used for text summarization.
* Udemy and Philip Lackner android courses for teaching me advanced Android programming using Kotlin.

![#c5f015](https://via.placeholder.com/15/c5f015/000000?text=+)<b> TODO :</b><br>
* Add feature to scrap text data from various web articles so that it can be directly summarized with a click. 
* Update UI (May update the current UI/Views in future with Composables)

