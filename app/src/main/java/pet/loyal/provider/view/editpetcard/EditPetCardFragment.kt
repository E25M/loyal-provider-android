package pet.loyal.provider.view.editpetcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.LightingColorFilter
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import pet.loyal.client.api.response.PetCardDataResponse
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.databinding.FragmentEditPatiantCardBinding
import pet.loyal.provider.model.Phase
import pet.loyal.provider.model.PhaseMessage
import pet.loyal.provider.model.RequestPTBMessage
import pet.loyal.provider.util.*
import pet.loyal.provider.view.dialog.ImageEnlargeFragment
import pet.loyal.provider.view.home.HomeScreen
import pet.loyal.provider.view.login.LoginActivity
import pet.loyal.provider.view.phasechange.PhaseListDialogFragment
import java.io.File
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
class EditPetCardFragment : Fragment(), PhaseMessageRecyclerViewAdapter.PhaseMessageItemListener,
    PhaseListDialogFragment.PhaseListDialogFragmentListener, EditPetCardPermissionListener {

    override fun onPermissionGranted(granted: Boolean, requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
            PERMISSION_REQUEST_READ_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }

    override fun onActivityResultListener(requestCode: Int, resultCode: Int, data: Intent?) {}

    private val CONTEXT_MENU_TAKE_A_PHOTO = 1
    private val CONTEXT_MENU_SELECT_A_PHOTO = 2
    private val REQUEST_TAKE_PICTURE = 1
    private val REQUEST_SELECT_PICTURE = 2
    private val PERMISSION_REQUEST_WRITE_STORAGE = 103
    private val PERMISSION_REQUEST_READ_STORAGE = 104
    private val REQUEST_CODE = 101

    private lateinit var fragmentEditPatiantCardBinding: FragmentEditPatiantCardBinding
    private lateinit var viewModel: EditPetCardViewModel
    private lateinit var preferenceManager: PreferenceManager

    private var selectedPhotoFile: File? = null
    private lateinit var selectedPhotoUri: Uri
    private var capturedImageCount = 0
    lateinit var selectedMessageId: String
    var selectedMessagePosition = 0
    private var imageGalleryList: HashMap<String, ArrayList<Uri>> = HashMap()
    private var imageIdsList: HashMap<String, ArrayList<String>> = HashMap()
    private var petCardDataResponse:PetCardDataResponse? = null
    private val phaseMessages = ArrayList<PhaseMessage>()
    private val initialControl = HashMap<String, String>()
    private val initialMessage = HashMap<String, String>()
    private var uploadingImagePosition = 0
    private var uploadingMessageIdPosition = 0

    private var customMessageId = 0
    private lateinit var appointmentId: String
    private var movingPhase = 0
    private var uploadedImageCount = 0

    private lateinit var mSocket: Socket

    companion object {
        fun newInstance() = EditPetCardFragment()
    }

    private val onNewMessageCard = Emitter.Listener { args ->

        activity!!.runOnUiThread {
            loadAppointment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        try {
            mSocket = IO.socket(BuildConfig.SOCKET_URL)
        } catch (ex: URISyntaxException) {
            showToast(activity!!, "Updating pet cards on real time is not working.")
        }

        mSocket.on("updateDashboard", onNewMessageCard)
        mSocket.let {
            it.connect().on(Socket.EVENT_CONNECT) {
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(0, CONTEXT_MENU_TAKE_A_PHOTO, 0, getString(R.string.text_take_a_photo))
        menu.add(0, CONTEXT_MENU_SELECT_A_PHOTO, 0, getString(R.string.text_choose_a_photo))
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            CONTEXT_MENU_TAKE_A_PHOTO -> checkPermissions()
            CONTEXT_MENU_SELECT_A_PHOTO -> selectPhoto()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                activity!!.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_WRITE_STORAGE)
            } else {
                captureImage()
            }
        } else {
            captureImage()
        }
    }

    //create file to save captured image by camera and save image
    private fun captureImage() {
        val root = Environment.getExternalStorageDirectory().toString()
        val loyalDir = File("$root${Constants.folder_loyal}")
        loyalDir.mkdirs()

        selectedPhotoFile = File(loyalDir, Constants.captured_pic_name + capturedImageCount + ".png")
//        selectedPhotoFile = File(loyalDir, Constants.captured_pic_name + ".png")
        if (selectedPhotoFile != null) {
            if (selectedPhotoFile!!.exists()) {
                selectedPhotoFile!!.delete()
            }
        }

        selectedPhotoFile!!.createNewFile()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        selectedPhotoUri = FileProvider.getUriForFile(activity!!,
            BuildConfig.APPLICATION_ID + ".provider", selectedPhotoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedPhotoUri)

        startActivityForResult(intent, REQUEST_TAKE_PICTURE)
    }

    private fun selectPhoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_STORAGE)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_SELECT_PICTURE)
    }

    private fun initDataBinding(){
        viewModel = ViewModelProviders.of(this).get(EditPetCardViewModel::class.java)
        fragmentEditPatiantCardBinding.lifecycleOwner = this
        fragmentEditPatiantCardBinding.viewModel = viewModel
        preferenceManager = PreferenceManager(context!!)
        viewModel.liveColor.value = resources.getDrawable(R.drawable.bg_general, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fragmentEditPatiantCardBinding = FragmentEditPatiantCardBinding
            .inflate(inflater, container, false)
        initDataBinding()

        val toolBar = fragmentEditPatiantCardBinding.toolbarHomeScreen
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        if (arguments != null){
            appointmentId = arguments!!.getString(Constants.extra_appointment_id, "")
        }

        setObservers()
        loadAppointment()

        viewModel.logo.value = preferenceManager.getfacilityLogo()

        fragmentEditPatiantCardBinding.btnUpdate.setOnClickListener {
            updatePhaseMessages()
        }

        fragmentEditPatiantCardBinding.btnCancel.setOnClickListener {
            if (isDataChanged()) {
                showConfirmNoUpdatedMessages(activity!!, getString(R.string.text_confirm_message),
                    getString(R.string.title_confirm))
            }else{
                activity!!.onBackPressed()
            }
        }

        fragmentEditPatiantCardBinding.layoutNext.setOnClickListener {
            val selectedPhaseList = ArrayList<Phase>()
            petCardDataResponse?.phases?.iterator()?.forEach { phase ->
                if (phase.id > petCardDataResponse?.appointment?.phase!!){
                    selectedPhaseList.add(phase)
                }
            }
            if (selectedPhaseList.size > 0) {
                loadPhaseChangeDialog(selectedPhaseList)
            }else{
                showToast(activity!!, "This is the final phase..")
            }
        }

        fragmentEditPatiantCardBinding.layoutPrevious.setOnClickListener {
            val selectedPhaseList = ArrayList<Phase>()
            petCardDataResponse?.phases?.iterator()?.forEach { phase ->
                if (phase.id < petCardDataResponse?.appointment?.phase!!){
                    selectedPhaseList.add(phase)
                }
            }
            if (selectedPhaseList.size > 0) {
                loadPhaseChangeDialog(selectedPhaseList)
            }else{
                showToast(activity!!, "This is the earliest phase..")
            }
        }

        (activity as HomeScreen).setEditCardPermissionListener(this)

        return fragmentEditPatiantCardBinding.root
    }

    private fun isDataChanged(): Boolean{
        phaseMessages.forEach {
            if (it.isSelected) {
                return true
            }
        }
        return false
    }

    private fun showUpdateConfirmation(){
        mSocket.emit("phaseUpdated", preferenceManager.getFacilityId())
        showUpdateConfirmPopup(activity!!,
            "Update sent to ${petCardDataResponse?.appointment?.petName} support network at" +
                    "\n ${getCurrentDateString()}, ${getCurrentTimeString()}",
            getString(R.string.text_info))
    }

    private fun updatePhaseMessages(){
        val selectedPTBMessages = getSelectedMessages()
        if (selectedPTBMessages != null) {
            if (selectedPTBMessages.isNotEmpty()) {
                if (imageGalleryList.size > 0) {
                    uploadPhoto()
                } else {
                    savePTBMessages()
                }
            } else {
                showToast(activity!!, getString(R.string.error_no_selected_ptb_message))
            }
        }
    }

    private fun loadPhaseChangeDialog(phaseList: ArrayList<Phase>){
        val phaseListDialogFragment = PhaseListDialogFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.extra_phase_list, phaseList)
        bundle.putString(Constants.extra_appointment_id, petCardDataResponse?.appointment?.id)
        bundle.putString(Constants.extra_pet_name, petCardDataResponse?.appointment?.petName)
        phaseListDialogFragment.arguments = bundle
        phaseListDialogFragment.setTargetFragment(this, REQUEST_CODE)
        activity!!.supportFragmentManager.beginTransaction().add(phaseListDialogFragment, "PhaseListDialog").commit()
    }

    private fun loadAppointment(){
        refreshAll()
        if (appointmentId != null && appointmentId.isNotEmpty()) {
            viewModel.getPetCardById(appointmentId, preferenceManager.getLoginToken())
        }
    }

    private fun savePTBMessages(){
        if ((movingPhase != 1) || (petCardDataResponse?.appointment?.phase!! == movingPhase)) {
            sendPTBMessages()
        }else{
            showConfirmPopup(activity!!, getString(R.string.msg_expected_phase), getString(R.string.title_confirm))
        }
    }

    private fun sendPTBMessages(){
        viewModel.savePTMMessages(
            getSelectedMessages()!!, preferenceManager.getLoginToken(),
            petCardDataResponse?.appointment?.phase!!, petCardDataResponse?.appointment?.id!!,
            preferenceManager.getFacilityId(), movingPhase
        )
    }

    private fun getSelectedMessages() : ArrayList<RequestPTBMessage>?{

        val requestMessageList = ArrayList<RequestPTBMessage>()
        phaseMessages.forEach {
            phaseMessage ->
            run {
                if (phaseMessage.isSelected) {
                    var message:String = if (phaseMessage.messageSpan != null){
                        phaseMessage.messageSpan.toString()
                    }else{
                        phaseMessage.message
                    }

                    if (message.isNotEmpty() && !(message.contains("<") && message.contains(">"))) {
                        val requestPTBMessage = RequestPTBMessage(
                            phaseMessage._id,
                            message,
                            phaseMessage.getIsCustom(),
                            false,
                            imageIdsList[phaseMessage._id]
                        )
                        requestMessageList.add(requestPTBMessage)
                    } else {
                        if (message.contains("<") && message.contains(">")) {
                            showErrorsIncompleteMessage(
                                message.substring(
                                    message.indexOf("<"),
                                    message.indexOf(">") + 1
                                )
                            )
                        } else {
                            if (phaseMessage.type == PhaseMessage.Type.CUSTOM_MESSAGE){
                                showPopup(
                                    activity!!,
                                    getString(R.string.error_message_please_apply),
                                    getString(R.string.text_info)
                                )
                            }else {
                                showPopup(
                                    activity!!,
                                    getString(R.string.error_message_connot_empty),
                                    getString(R.string.text_info)
                                )
                            }
                        }
                        return null
                    }
                }
            }
        }

        val phaseChangeMessage = phaseMessages[0]
        if (phaseChangeMessage.type == PhaseMessage.Type.PHASE_CHANGE){
            requestMessageList.add(RequestPTBMessage(
                customMessageId++.toString(),
                phaseChangeMessage.message,
                isCustom = false,
                isPhaseChange = true
            ))
        }
        return requestMessageList
    }

    private fun setObservers(){
        viewModel.petCardResponse.observe(this, Observer {
                petCardResponse ->
            run {
                if (petCardResponse != null) {
                    if (petCardResponse.throwable != null) {
                        if (isJSONValid(petCardResponse.throwable?.message!!)) {
                            val signInResponse = Gson().fromJson(
                                petCardResponse.throwable?.message, AppVersionResponse::class.java
                            )
                            if (signInResponse.statusCode == 401) {
                                redirectToLogin()
                            }else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive){
                                showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                    getString(R.string.text_info))
                            }else{
                                showPopup(activity!!, petCardResponse.throwable?.message!!, getString(R.string.text_info))
                            }
                        } else {
                            showPopup(activity!!, petCardResponse.throwable?.message!!, getString(R.string.text_info))
                        }
                    } else if (petCardResponse.petCardResponse != null){
                        petCardDataResponse = petCardResponse.petCardResponse?.data
                        showCardDetails()
                    }
                }
                viewModel.liveProgressDialog.value = View.GONE
            }
        })

        viewModel.savePTBMessageResponse.observe(this, Observer { savePTBMessageResponse ->
            run {
                if (savePTBMessageResponse != null){
                    if (savePTBMessageResponse.throwable != null){
                        if (isJSONValid(savePTBMessageResponse.throwable?.message!!)) {

                            val signInResponse = Gson().fromJson(
                                savePTBMessageResponse.throwable?.message, AppVersionResponse::class.java
                            )
                            if (signInResponse.statusCode == 401) {
                                redirectToLogin()
                            }else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive){
                                showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                    getString(R.string.text_info))
                            }else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == "PtbMessage validation failed: message: Path `message` is required."){
                                showPopup(activity!!, "Please enter message.",
                                    getString(R.string.text_info))
                            } else if (signInResponse.statusCode == 400){
                                showPopup(activity!!, getMessageUpdateError(signInResponse.errorMessage!!),
                                    getString(R.string.text_info))

                            }else{
                                showPopup(activity!!, savePTBMessageResponse.throwable?.message!!, getString(R.string.text_info))
                            }
                        } else {
                            showPopup(activity!!, savePTBMessageResponse.throwable?.message!!, getString(R.string.text_info))
                        }
                    }else{
                        showUpdateConfirmation()
                    }
                }
                viewModel.liveProgressDialog.value = View.GONE
                viewModel.liveProgressPercentage.value = View.GONE
            }
        })
    }

    private fun showCardDetails(){
        if (petCardDataResponse != null){
            val petCardDataResponse = this.petCardDataResponse!!
            val appointment = petCardDataResponse.appointment
            if (appointment != null){
                val phases = petCardDataResponse.phases
                movingPhase = appointment.phase
                viewModel.livePetName.value = appointment.petName + ", " + appointment.parentLastName
                viewModel.livePetImage.value = appointment.petImage

                if (appointment.petBreed != null) {
                    viewModel.liveBreedSpecies.value = appointment.petBreed +
                            ", " + appointment.petSpecies + "\n" + appointment.petGender.replace("_", " ")
                }else{
                    viewModel.liveBreedSpecies.value = appointment.petSpecies + "\n" + appointment.petGender
                }
//                viewModel.liveGendar.value = appointment.petGender
                phases.iterator().forEach {
                    if (it.id == appointment.phase){
                        viewModel.livePhase.value = it.name
                    }
                }

                val bgColor:Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPhaseColors(appointment.phase, activity!!)
                }else{
                    getPhaseColorsOld(appointment.phase, activity!!)
                }

                val drawableBgType =  resources.getDrawable(R.drawable.bg_general, null)
                val colorFilter = LightingColorFilter(bgColor, bgColor)
                drawableBgType.colorFilter = colorFilter
                viewModel.liveColor.value = drawableBgType

                val drawableTop =  resources.getDrawable(R.drawable.bg_edit_patient_card_deco, null)
                drawableTop.colorFilter = colorFilter
                viewModel.livePhaseColorDrawable.value = drawableTop

                petCardDataResponse.ptbMessageTemplates.iterator().forEach { messageTemplate ->
                    run {
                        var imageGallery: ArrayList<Uri>? = null
                        if (imageGalleryList.containsKey(messageTemplate._id)) {
                            imageGallery = imageGalleryList[messageTemplate._id]
                        }

                        var message:String
                        message = if (messageTemplate.editable && messageTemplate.controlMessage != null){
                            messageTemplate.controlMessage
                        }else{
                            messageTemplate.message
                        }

                        message = message.replace("PetName",
                            petCardDataResponse.appointment.petName, true)

                        if (messageTemplate.fetchable){
                            var replaceableText = ""
                            when(messageTemplate.fetchableValue){
                                "PhoneNumber" -> {
                                    val phoneNo = preferenceManager.getFacilityPhone()
                                    if (phoneNo.length == 10) {
                                        replaceableText = "(" + phoneNo.substring(0, 3) +
                                                ") " + phoneNo.substring(3, 6) +
                                                "-" + phoneNo.substring(6)
                                    }
                                }
                                "FacilityName" -> replaceableText =
                                    preferenceManager.getFacilityName()
                            }
                            message = message.replace(
                                messageTemplate.fetchableValue,
                                replaceableText,
                                true)
                        }

                        var messageSent = false

                        petCardDataResponse.ptbSentMessages.iterator().forEach { sentMessage ->
                            if (sentMessage.phaseMessageId == messageTemplate._id){
                                messageSent = true
                            }
                        }

                        var phaseMessage: PhaseMessage?
                        if (!messageSent) {
                            phaseMessage = PhaseMessage(
                                messageTemplate._id, messageTemplate.phaseId,
                                message, messageTemplate.editable, imageGallery,
                                messageTemplate.control, messageTemplate.controlMessage,
                                messageTemplate.value, messageTemplate.placeholder
                            )
                            if (phaseMessage.control != null) {
                                initialControl[phaseMessage._id] = phaseMessage.control!!
                                initialMessage[phaseMessage._id] = phaseMessage.controlMessage!!
                            }
                            phaseMessages.add(phaseMessage)
                        }
                    }
                }

                addCustomMessage(null)

                petCardDataResponse.ptbSentMessages.iterator().forEach { sentMessage ->
                    val imageGallery = ArrayList<Uri>()
                    if (sentMessage.gallery != null) {
                        sentMessage.gallery.iterator().forEach {
                            imageGallery.add(Uri.parse(Constants.url_cloudinary_news_feed + it))
                        }
                    }

                    var message = sentMessage.message
                    message = message.replace("PetName",
                        petCardDataResponse.appointment.petName, true)

                    phaseMessages.add(PhaseMessage(sentMessage.phaseMessageId, sentMessage.status,
                        sentMessage._id, sentMessage.phaseId, sentMessage.appointmentId,
                        message, sentMessage.dateTime, imageGallery))
                }

                val phaseMessageRecyclerViewAdapter = PhaseMessageRecyclerViewAdapter(
                    phaseMessages, this)
                fragmentEditPatiantCardBinding.recyclerViewMessages.setHasFixedSize(true)
                fragmentEditPatiantCardBinding.recyclerViewMessages.layoutManager =
                    LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false)
                fragmentEditPatiantCardBinding.recyclerViewMessages.adapter =
                    phaseMessageRecyclerViewAdapter
            }
        }
    }

    private fun formatMessage(oldMessage: String) : String{

        var message = oldMessage

        message = message.replace("PetName",
            petCardDataResponse!!.appointment!!.petName, true)

        message = message.replace("PhoneNumber",
            getPhoneNo(), true)

        message = message.replace("FacilityName",
            preferenceManager.getFacilityName(), true)

        return message
    }

    private fun getPhoneNo() :String{
        val phoneNo = preferenceManager.getFacilityPhone()
        if (phoneNo.length == 10) {
            return "(" + phoneNo.substring(0, 3) +
                    ") " + phoneNo.substring(3, 6) +
                    "-" + phoneNo.substring(6)
        }
        return ""
    }

    private fun refreshAll(){
        imageGalleryList.clear()
        imageIdsList.clear()
        phaseMessages.clear()

        uploadingImagePosition = 0
        uploadingMessageIdPosition = 0
        petCardDataResponse = null
    }

    private fun addCustomMessage(position: Int?){
        customMessageId++
//            hideKeyboard()
        val newCustomMessage = PhaseMessage(customMessageId.toString(), petCardDataResponse?.appointment?.phase!!,
            petCardDataResponse?.appointment?.id, false)
            phaseMessages.add(0, newCustomMessage)
    }

    private fun showAddedImage(){
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == selectedMessageId){
                phaseMessage.imageGallery = imageGalleryList[selectedMessageId]
                phaseMessage.isSelected = true
                phaseMessage.canAddPhoto = imageGalleryList[selectedMessageId]!!.size < 10
            }
        }
        notifyItemChange()
    }

    private fun notifyItemChange(){
        if (fragmentEditPatiantCardBinding.recyclerViewMessages.adapter != null) {
            fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!
                .notifyItemChanged(selectedMessagePosition)
        }
    }

    private fun uploadPhoto() {

        viewModel.liveProgressDialog.value = View.VISIBLE
//        viewModel.liveProgressPercentage.value = View.VISIBLE

        val publicId = "${Calendar.getInstance().timeInMillis}_${preferenceManager.getUserId()}"
        val imageUri = imageGalleryList[imageGalleryList.keys.elementAt(uploadingMessageIdPosition)]?.get(uploadingImagePosition)!!

        try {
            MediaManager.get().upload(imageUri)
                .option("public_id", publicId)
                .option("folder", "newsfeed/")
                .unsigned(BuildConfig.CLOUDINARY_PRESET)
                .callback(object : UploadCallback {

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val messageId =  imageGalleryList.keys.elementAt(uploadingMessageIdPosition)

                        if (!imageIdsList.containsKey(messageId)){
                            imageIdsList[messageId] = arrayListOf()
                        }
                        imageIdsList[messageId]?.add(publicId)

                        if (uploadingMessageIdPosition + 1 == imageGalleryList.keys.size
                            && uploadingImagePosition + 1 == imageGalleryList[messageId]?.size){

                            savePTBMessages()
                        }else{
                            if (imageGalleryList[messageId]?.size == uploadingImagePosition + 1){
                                uploadingMessageIdPosition ++
                                uploadingImagePosition = 0
                            }else {
                                uploadingImagePosition++
                            }

                            uploadPhoto()
                        }

                        uploadedImageCount ++
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
//                        viewModel.livePercentage.value = ((uploadedImageCount*100/getAllImageCount())
//                                + ((bytes*(100/getAllImageCount())/totalBytes))).toString() + "%"
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        viewModel.liveProgressDialog.value = View.GONE
                        showPopup(activity!!, getString(R.string.error_upload_failed), getString(R.string.text_error))
                    }

                    override fun onStart(requestId: String?) {}

                }).dispatch()
        }catch (e:OutOfMemoryError){
            showPopup(activity!!, getString(R.string.error_upload_failed), getString(R.string.text_error))
        }
    }

    private fun redirectToLogin() {
        preferenceManager.deleteSession()
        showToast(context!!, getString(R.string.txt_logged_out))
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun onClickAddPhotos(view: View, position: Int, messageId: String) {
        selectedMessageId = messageId
        selectedMessagePosition = position
//        registerForContextMenu(view)
//        activity!!.openContextMenu(view)
        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_PICTURE && resultCode ==
            Activity.RESULT_OK && data != null) {
            if (data.data != null){
                val imageUri = data.data.toString()
                addImageUriToGallery(Uri.parse(imageUri))
            }else if(data.clipData != null){
                val itemCount = data.clipData.itemCount
                for (item in itemCount downTo 1 step 1){
                    addImageUriToGallery(data.clipData.getItemAt(item - 1).uri)
                }
            }
        }

        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            addImageUriToGallery(selectedPhotoUri)
            capturedImageCount ++
            resetOrientation(selectedPhotoFile)
        }else if (resultCode == Activity.RESULT_CANCELED){
            return
        }
        showAddedImage()
    }

    //Add selected image Uri to gallery list
    private fun addImageUriToGallery(uri: Uri?){
        if (uri != null){
            if (!imageGalleryList.containsKey(selectedMessageId)){
                imageGalleryList[selectedMessageId] = ArrayList()
            }
            if (imageGalleryList[selectedMessageId]?.size!! < 10) {
                imageGalleryList[selectedMessageId]?.add(0, uri)
                selectedPhotoUri = Uri.EMPTY
            }else{
                showToast(activity!!, getString(R.string.text_10_photos_limit))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
            PERMISSION_REQUEST_READ_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }

    override fun onClickDelete(positionImage: Int, position: Int, messageId: String) {
        imageGalleryList[messageId]?.removeAt(positionImage)
        selectedMessagePosition = position
        phaseMessages.iterator().forEach { phaseMessage ->
            run {
                if (phaseMessage._id == messageId){
                    if (phaseMessage.type != PhaseMessage.Type.CUSTOM_MESSAGE
                        && positionImage == 0 && imageGalleryList[messageId]!!.size == 0){

                        phaseMessage.isSelected = false
                    }else {
                        phaseMessage.canAddPhoto = true
                    }
                }
            }
        }
        notifyItemChange()
    }

    override fun onClickImage(positionImage: Int, position: Int, messageId: String) {
        enlargeImage(phaseMessages[position].imageGallery?.get(positionImage).toString())
    }

    private fun enlargeImage(url: String) {
        val bundle = Bundle()
        bundle.putString(Constants.extra_image_id, url)
        val imageEnlargeFragment = ImageEnlargeFragment()
        imageEnlargeFragment.arguments = bundle
        imageEnlargeFragment.show(fragmentManager!!, "")
    }

    override fun onClickTick(isChecked: Boolean, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            run {
                if (phaseMessage._id == messageId) {
                    phaseMessage.isSelected = isChecked
                    if (!isChecked){
                        phaseMessage.imageGallery = null
                        imageGalleryList.remove(messageId)
                        phaseMessage.messageSpan = null
                        if (initialControl.contains(messageId)) {
                            phaseMessage.control = initialControl[messageId]!!
                            phaseMessage.message = formatMessage(initialMessage[messageId]!!)
                        }
                    }
                }
            }
        }

        fragmentEditPatiantCardBinding.recyclerViewMessages.post {
            run {
                fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!.notifyItemChanged(position)
            }
        }
    }

    override fun onClickTickCustom(isChecked: Boolean, position: Int, messageId: String) {
        val iterator = phaseMessages.iterator()
        iterator.forEach { phaseMessage ->
            run {
                if (phaseMessage.type == PhaseMessage.Type.CUSTOM_MESSAGE) {
                    if (phaseMessage._id == messageId) {
                        phaseMessage.isSelected = isChecked
                        if (!isChecked) {
                            phaseMessage.imageGallery = null
                            imageGalleryList.remove(messageId)
                            phaseMessage.message = ""
                            if ((position != 0) || (position == 0)
                                && (phaseMessages[1].type == PhaseMessage.Type.CUSTOM_MESSAGE)) {

                                iterator.remove()
                            }
                        }

                        fragmentEditPatiantCardBinding.recyclerViewMessages.post{
                            fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!.notifyItemChanged(position)
                        }
                        return@forEach
                    }
                }
            }
        }
    }

    override fun onEditMessage(message: String, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == messageId){
                phaseMessage.message = message
                phaseMessage.isSelected = true
            }
        }

        fragmentEditPatiantCardBinding.recyclerViewMessages.post {
            run {
                fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!.notifyItemChanged(position)
            }
        }
    }

    override fun onEditMessage(message: Spannable, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == messageId){
                phaseMessage.messageSpan = message
                phaseMessage.message = message.toString()
                phaseMessage.isSelected = true

                fragmentEditPatiantCardBinding.recyclerViewMessages.post {
                    run {
                        fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun onEditMessageEditText(message: Spannable, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == messageId){
                phaseMessage.messageSpan = message
                phaseMessage.message = message.toString()
                phaseMessage.isSelected = true

                fragmentEditPatiantCardBinding.recyclerViewMessages.post {
                    fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onEditMessageCustom(message: String, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == messageId){
                phaseMessage.message = message
                phaseMessage.isSelected = true
            }
        }
    }

    override fun onPhaseChangeSuccess(message: String, movingPhase: Int) {
        if (phaseMessages[0].type == PhaseMessage.Type.PHASE_CHANGE){
            phaseMessages.removeAt(0)
        }
        this.movingPhase = movingPhase
        if (message != "true") {
            phaseMessages.add(0, PhaseMessage(message))
        }else{
            phaseMessages.add(0, PhaseMessage(""))
            savePTBMessages()
        }
        fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!.notifyDataSetChanged()
    }

    override fun onPhaseChangeFailed(errorMessage: String) {
        showPopup(activity!!, getPhaseChangeError(errorMessage), getString(R.string.text_error))
    }

    private fun getPhaseChangeError(errorMessage: String): String {
        return when(errorMessage){
            Constants.pet_is_not_active -> "${petCardDataResponse?.appointment?.petName} " +
                    getString(R.string.text_please_active_pet)
            Constants.parent_is_not_active -> "${petCardDataResponse?.appointment?.parentFirstName}" +
                    " ${petCardDataResponse?.appointment?.parentLastName} " +
                    getString(R.string.error_please_active_parent)
            Constants.there_are_another_ongoing_appointments_for_this_pet ->
                "${petCardDataResponse?.appointment?.petName} " +
                        getString(R.string.text_please_complete_ongoing_appointment)
            else -> getString(R.string.text_phase_change_failed)
        }
    }

    private fun getMessageUpdateError(errorMessage: String): String {
        return when(errorMessage){
            Constants.pet_is_not_active -> "${petCardDataResponse?.appointment?.petName} " +
                    getString(R.string.error_please_active_pet)
            Constants.parent_is_not_active -> "${petCardDataResponse?.appointment?.parentFirstName}" +
                    " ${petCardDataResponse?.appointment?.parentLastName} " +
                    getString(R.string.error_active_parent)
            Constants.there_are_another_ongoing_appointments_for_this_pet ->
                "${petCardDataResponse?.appointment?.petName} " +
                        getString(R.string.text_please_complete_ongoing_appointment)
            else -> getString(R.string.text_phase_change_failed)
        }
    }

    override fun onAddCustomMessage(position: Int) {
        addCustomMessage(position)
    }

    fun showPopup(context: Context, message: String, title: String) {
        val aDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
//                loadAppointment()
            }
            .create()
        aDialog.show()
    }

    private fun showConfirmPopup(context: Context, message: String, title: String) {
        val aDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(R.string.yes) { _, _ ->
               sendPTBMessages()
            }
            .setNegativeButton(R.string.no){_,_->

            }
            .create()
        aDialog.show()
    }

    private fun showConfirmNoUpdatedMessages(context: Context, message: String, title: String) {
        val aDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(R.string.yes) { _, _ ->
                activity!!.onBackPressed()
            }
            .setNegativeButton(R.string.no){_,_->

            }
            .create()
        aDialog.show()
    }

    private fun showUpdateConfirmPopup(context: Context, message: String, title: String) {

        var timer:CountDownTimer? = null
        val aDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                activity!!.onBackPressed()
                if (timer != null){
                    timer!!.cancel()
                }
            }.create()

        timer = object : CountDownTimer(1500, 1500){
            override fun onFinish() {
                activity!!.onBackPressed()
                aDialog.dismiss()
            }
            override fun onTick(millisUntilFinished: Long) {}
        }

        aDialog.show()
        timer.start()
    }

    private fun showErrorsIncompleteMessage(errorCode: String){
        var errorMessage: String
        errorMessage = when(errorCode){
            "<SELECT TIME>" -> "Select a time"
            "<PLEASE SELECT>" -> "Select at least one option"
            else -> "Message cannot be empty"
        }
        showPopup(activity!!, errorMessage, getString(R.string.text_info))
    }

    private fun hideKeyboard() {
        val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity!!.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

        mSocket.off("updateDashboard", onNewMessageCard)

        val root = Environment.getExternalStorageDirectory().toString()
        val loyalDir = File("$root${Constants.folder_loyal}")
        loyalDir.mkdirs()

        for (i in 0 until capturedImageCount) {
            val file = File(loyalDir, Constants.captured_pic_name + i + ".png")

            if (file != null) {
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }
}

