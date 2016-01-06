# AtlassianTest

Time spent on task: about couple of days

## Tech info

I approached this task as if I was working on the real piece of software. In such case, the design of code is utterly important at the first phase. I designed it in object-oriented manner utilising some classic pattern to allow high flexibility and extension. 
Briefly, the idea is to have a single factory responsible for producing objects for each message. Such objects contain detected tags which can be converted to any format (JSON in our case). Factory in its turn contains a list of registered signatures which act as a descriptors with processing kernels. In some way resembling a visitor pattern, the original string is then fed to each of the registered signature to be analyzed internally. Kernels produce the results which are accumulated during the processing by the factory and return upon completion as a Tagged Screen - the object mentioned above.
Such approach allows the following:
- Create other custom tag signatures with different algorithms and processing logic
- Extend the factory with other features, such as explicit selection of signatures
- Parallel execution, if needed
- Loose dependency between objects
- Easy unit testing
- A signature object can be used as a standalone entity for matching routines

The logic is separated to libTagFinder Android library which can be used in other projects.
As for the application, I used my test framework as a base. As UI was not a part of the original requirement, it is very minimal and created just to allow interaction with the library and get its output.

## Features 
- Creating custom tag descriptors with any combinations of delimiters including predefined ones such as WhiteSpace, NonWord and Endless
- Helpful flags which alter the matching logic for powerful features which include: Delimiter Inclusions, Nested Tags, Case Insensitivity, String start requirement (for chat commands), etc.
- Supporting multiple delimiters per tag
- Regex support
- JSON output of resulting Tagged String
- Separate resulting object for further extensions (like more output formats and analysis)
- Builtin web page retrieval for data prefetching

## Examples
There are few tags registered in the application. Part of them is per Atlassian task requirements:
@mentions, (emoticons) and URL links (http://; https://; www.)
I also included "/me" as an example of using the library for commands parsing and nested tags.

## Dependencies
The only external library which is used is Jsoup. This was required for easy Html parsing and network connection to satisfy the original goals.

##Testing
There are some unit tests created. 
- TagFinderUnitTests are meant for testing the library and its logic.
- AtlassianTaskTest contains a test for task criteria

## Known issues and limitations
- Android's JSONObject toString() escapes the slash characters. This is officially known and treated as a feature. It does not alter the JSON structure, however. So JSONObject can be used for networking and serialisation/deserealisation.
- There are no network connection checks and no progress indication. As it was not a part of the task, I skipped such features in favour of more important things. If network request fails, "title" parameter is simply omitted.
- The app was not tested on Tablets, although it should work totally fine there.
- I deliberately did not implement any hardcore matching or string search algorithm as it does not make sense to optimise performance when dealing with short strings (and that's the case due to the nature of chats). So, good old indexOf is still there!

