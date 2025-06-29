package com.example.news26

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.RoomDatabase
import com.example.news26.databinding.ActivityMainBinding
import com.example.news26.db.ArticleDatabase
import com.example.news26.fragments.About
import com.example.news26.fragments.Favourite
import com.example.news26.fragments.Home
import com.example.news26.fragments.Setting
import com.example.news26.repository.NewsRepository
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth  : FirebaseAuth
    private lateinit var binding : ActivityMainBinding

    private lateinit var drawer : DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

     // this will be shared across fragments
    lateinit var viewModel: NewsViewModel ;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        // configuration for header view
        val headerView =  navigationView.getHeaderView(0)
        val headername = headerView.findViewById<TextView>(R.id.userProfile)
        val headerEmail = headerView.findViewById<TextView>(R.id.emailProfile)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
        // so update the header
        if (auth.currentUser != null){
            val db = FirebaseFirestore.getInstance()
            val userId = auth.currentUser?.uid.toString()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener {
                    doc ->
                    headername.text = doc.getString("name")
                    headerEmail.text = doc.getString("email")
                }
        }

        //
        drawer = binding.main
        toolbar = binding.toolBar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

     // setting an listener to open the drawer
        binding.imageToolbar.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

     //on back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START)
                }else{
                    val fm  = supportFragmentManager
                    if (fm.backStackEntryCount>0){
                        fm.popBackStack()
                    }else{
                        finish()
                    }
                }
            }
        })

//         by default it should be in  home fragment
        if (savedInstanceState == null) navigateFragment(Home())

//        database instantiated
       val database = ArticleDatabase(this)
       val newsRepository = NewsRepository(database)
       val viewModelProviderFactory = ViewModelProviderFactory(application,newsRepository)
       viewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           R.id.home -> {
               navigateFragment(Home())
               drawer.closeDrawer(GravityCompat.START)
           }
            R.id.setting -> {
                navigateFragment(Setting())
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.about -> {
                navigateFragment(About())
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.fav -> {
                navigateFragment(Favourite())
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.logout ->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }


        }
        return true
    }

    private fun navigateFragment(fragment: Fragment){
        val manager = supportFragmentManager
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        manager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }
}