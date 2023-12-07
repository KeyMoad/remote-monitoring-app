import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Server Management',
      theme: ThemeData(
        visualDensity: VisualDensity.adaptivePlatformDensity,
        scaffoldBackgroundColor:
            const Color.fromARGB(255, 41, 41, 41), // Dark background color
      ),
      home: const HomePage(),
    );
  }
}

class Service {
  final String name;
  final String status;

  Service({required this.name, required this.status});

  Future<void> start() async {
    // Mock API call for starting the service
  }

  Future<void> stop() async {
    // Mock API call for stopping the service
  }

  Future<void> restart() async {
    // Mock API call for restarting the service
  }
}

class CronJob {
  final String name;
  final String schedule;

  CronJob({required this.name, required this.schedule});

  Future<void> add() async {
    // Mock API call for adding a cron job
  }

  Future<void> delete() async {
    // Mock API call for deleting a cron job
  }
}

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  void handleProfileEdit(BuildContext context) {
    // Implement your logic for profile editing here
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: MediaQuery.of(context).size.height * 0.075,
            pinned: true,
            flexibleSpace: LayoutBuilder(
              builder: (BuildContext context, BoxConstraints constraints) {
                return FlexibleSpaceBar(
                  title: SizedBox(
                    width: constraints.maxWidth,
                    child: Padding(
                      padding: EdgeInsets.symmetric(
                        horizontal: MediaQuery.of(context).size.width * 0.05,
                      ),
                      child: const Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            'Server Management',
                            style: TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                              fontSize: 15.0,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  background: Container(
                    color: Colors.red, // Netflix Red
                  ),
                );
              },
            ),
            leading: IconButton(
              icon: const Icon(Icons.menu),
              onPressed: () {
                // Implement action for hamburger menu
              },
            ),
            actions: [
              IconButton(
                icon: const Icon(Icons.edit),
                onPressed: () {
                  handleProfileEdit(context); // Pass the context here
                },
              ),
            ],
          ),
          SliverList(
            delegate: SliverChildListDelegate(
              [
                _buildSection(
                  title: 'Server Metrics',
                  backgroundColor: Color.fromARGB(255, 78, 36, 36), // Dark Grey
                  context: context, // Pass the context here
                ),
                _buildSection(
                  title: 'Services',
                  backgroundColor: Color(0xFF870D0F), // Dark Red
                  context: context, // Pass the context here
                ),
                _buildSection(
                  title: 'Cron Jobs',
                  backgroundColor: Color(0xFF1D1819), // Dark Maroon
                  context: context, // Pass the context here
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSection({
    required String title,
    required Color backgroundColor,
    required BuildContext context, // Receive the context here
  }) {
    return Container(
      color: backgroundColor,
      padding: EdgeInsets.symmetric(
        horizontal: MediaQuery.of(context).size.width * 0.05,
        vertical: MediaQuery.of(context).size.height * 0.02,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 20.0,
            ),
          ),
          // Replace this with your API calls and UI logic
          // Display relevant data or buttons based on the section
        ],
      ),
    );
  }
}
