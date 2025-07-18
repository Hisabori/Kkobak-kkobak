import 'package:flutter/material.dart';

void main() {
  runApp(const KkobakApp());
}

class KkobakApp extends StatelessWidget {
  const KkobakApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Kkobak Flutter',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('꾸박꾸박'),
      ),
      body: const Center(
        child: Text('Hello from Flutter!'),
      ),
    );
  }
}
